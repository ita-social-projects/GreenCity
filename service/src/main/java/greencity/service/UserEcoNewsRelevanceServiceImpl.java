package greencity.service;

import greencity.dto.econews.UserEcoNewsRelevanceResponseDto;
import greencity.entity.EcoNews;
import greencity.entity.User;
import greencity.entity.UserEcoNewsRelevance;
import greencity.entity.localization.TagTranslation;
import greencity.enums.Language;
import greencity.repository.EcoNewsRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserEcoNewsRelevanceRepo;
import greencity.repository.UserRepo;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserEcoNewsRelevanceServiceImpl implements UserEcoNewsRelevanceService {
    private final UserEcoNewsRelevanceRepo userEcoNewsRelevanceRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final UserRepo userRepo;
    private final AcceptLanguageDisplayService acceptLanguageDisplayService;

    private static final String AI_USER_EMAIL = "ai.generated@example.com";
    private static final double RELEVANCE_THRESHOLD = 0.6;
    private static final Set<String> STOP_WORDS = Set.of(
        "a", "an", "the", "and", "or", "but", "in",
        "on", "at", "to", "for", "of", "with", "by"
    );

    @Transactional
    @Override
    public void calculateRelevanceForAIGeneratedNews(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        String language = resolveLanguageForUser();
        List<EcoNews> aiGeneratedNews = ecoNewsRepo.findAllAIGeneratedSince(AI_USER_EMAIL, ZonedDateTime.now().minusDays(365));
        List<String> habitNames = habitAssignRepo.fetchHabitNamesByUserId(userId);

        List<UserEcoNewsRelevance> relevances = aiGeneratedNews.stream()
            .map(news -> {
                UserEcoNewsRelevance relevance = userEcoNewsRelevanceRepo
                    .findByUserIdAndEcoNewsId(userId, news.getId())
                    .orElseGet(() -> UserEcoNewsRelevance.builder().build());
                double score = calculateRelevanceScore(news, habitNames, language);
                relevance.setUser(user);
                relevance.setEcoNews(news);
                relevance.setRelevance(score);
                return relevance;
            })
            .collect(Collectors.toList());

        userEcoNewsRelevanceRepo.saveAll(relevances);
        userEcoNewsRelevanceRepo.flush();
    }

    @Override
    public List<UserEcoNewsRelevanceResponseDto> getRelevantNewsForUser(Long userId) {
        return userEcoNewsRelevanceRepo.findByUserIdAndAIGeneratedNews(
            userId, AI_USER_EMAIL, RELEVANCE_THRESHOLD
            ).stream()
            .map(relevance -> new UserEcoNewsRelevanceResponseDto(
                relevance.getId(),
                relevance.getEcoNews().getId(),
                relevance.getRelevance()
            )).collect(Collectors.toList());
    }

    private double calculateRelevanceScore(EcoNews ecoNews, List<String> habitNames, String language) {
        if (ecoNews == null || habitNames == null || habitNames.isEmpty()) {
            return 0.0;
        }
        Set<String> tagNames = ecoNews.getTags().stream()
            .flatMap(tag -> tag.getTagTranslations().stream()
                .filter(t -> t.getLanguage().getCode().equalsIgnoreCase(language))
                .map(TagTranslation::getName))
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

        Set<String> habitSet = habitNames.stream()
            .map(String::toLowerCase).collect(Collectors.toSet());
        double tagScore = calculateJaccardSimilarity(tagNames, habitSet);

        String content = (ecoNews.getTitle() + " " + ecoNews.getText()).toLowerCase();
        Set<String> contentWords = Arrays.stream(content.split("\\W+"))
            .filter(word -> !STOP_WORDS.contains(word) && word.length() > 2)
            .collect(Collectors.toSet());
        double keyWordScore = calculateKeywordOverlap(contentWords, habitSet);
        double recencyScore = calculateRecencyScore(ecoNews.getCreationDate());
        double finalScore = 0.4 * tagScore + 0.4 * keyWordScore + 0.2 * recencyScore;
        return Math.min(Math.max(finalScore, 0.0), 1.0);
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() && set2.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    private double calculateKeywordOverlap(Set<String> contentWords, Set<String> habitNames) {
        if (contentWords.isEmpty() && habitNames.isEmpty()) {
            return 0.0;
        }
        long matches = contentWords.stream()
            .filter(habitNames::contains)
            .count();
        return (double) matches / Math.max(contentWords.size(), habitNames.size());
    }

    private double calculateRecencyScore(ZonedDateTime creationDate) {
        if (creationDate == null) {
            return 0.0;
        }
        long daysOld = Duration.between(creationDate, ZonedDateTime.now()).toDays();
        if (daysOld <= 7) {
            return 1.0;
        } else if (daysOld <= 365) {
            return 0.0;
        }else {
            return (365.0 - daysOld) / (365.0 - 7.0);
        }
    }

    private String resolveLanguageForUser() {
        String languageCode = acceptLanguageDisplayService.resolveLanguage();
        return Language.fromCode(languageCode).getCode();
    }
}
