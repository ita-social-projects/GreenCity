package greencity.service;

import static greencity.constant.UserEcoNewsRelevanceConstants.*;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.UserEcoNewsRelevanceResponseDto;
import greencity.entity.EcoNews;
import greencity.entity.User;
import greencity.entity.UserEcoNewsRelevance;
import greencity.enums.Language;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserEcoNewsRelevanceRepo;
import greencity.repository.UserRepo;
import static java.lang.Math.clamp;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public void calculateRelevanceForAIGeneratedNews(Long userId) {
        User user = fetchUser(userId);
        String language = resolveLanguageForUser();
        List<EcoNewsDto> aiGeneratedNewsDtos = prepareAIGeneratedNewsDtos();
        List<String> habitNames = fetchUserHabitNames(userId);
        List<UserEcoNewsRelevance> relevances = computeRelevances(userId, user, aiGeneratedNewsDtos, habitNames, language);
        saveRelevances(relevances);
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
            )).toList();
    }

    @Override
    public double calculateRelevanceScore(EcoNewsDto ecoNews, List<String> habitNames, String language) {
        if (ecoNews == null || habitNames == null || habitNames.isEmpty()) {
            return 0.0;
        }

        Set<String> tagNames = extractTags(ecoNews);
        Set<String> habitSet = toLowerCaseSet(habitNames);

        double tagScore = calculateJaccardSimilarity(tagNames, habitSet);
        double keyWordScore = calculateKeywordOverlap(extractContentKeywords(ecoNews), habitSet);
        double recencyScore = calculateRecencyScore(ecoNews.getCreationDate());

        return calculateFinalScore(tagScore, keyWordScore, recencyScore);
    }

    private Set<String> extractTags(EcoNewsDto ecoNews) {
        return ecoNews.getTags().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }

    private Set<String> extractContentKeywords(EcoNewsDto ecoNews) {
        String content = (ecoNews.getTitle() + EMPTY_STRING + ecoNews.getContent()).toLowerCase();
        return Arrays.stream(content.split(REGEX_SPLIT_PATTERN))
            .filter(word -> !STOP_WORDS.contains(word) && word.length() > MIN_WORD_LENGTH)
            .collect(Collectors.toSet());
    }

    private Set<String> toLowerCaseSet(List<String> list) {
        return list.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }

    private double calculateFinalScore(
        double tagScore, double keyWordScore, double recencyScore
    ) {
        double finalScore = TAG_SCORE_WEIGHT * tagScore
            + KEYWORD_SCORE_WEIGHT * keyWordScore
            + RECENCY_SCORE_WEIGHT * recencyScore;
        return clamp(finalScore, MIN_SCORE, MAX_SCORE);
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() && set2.isEmpty()) {
            return MIN_SCORE;
        }
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    private double calculateKeywordOverlap(Set<String> contentWords, Set<String> habitNames) {
        if (contentWords.isEmpty() && habitNames.isEmpty()) {
            return MIN_SCORE;
        }
        long matches = contentWords.stream()
            .filter(habitNames::contains)
            .count();
        return (double) matches / Math.max(contentWords.size(), habitNames.size());
    }

    private double calculateRecencyScore(ZonedDateTime creationDate) {
        if (creationDate == null) {
            return MIN_SCORE;
        }
        long daysOld = Duration.between(creationDate, ZonedDateTime.now()).toDays();
        if (daysOld <= DAYS_IN_A_WEEK) {
            return MAX_SCORE;
        } else if (daysOld <= DAYS_IN_A_YEAR) {
            return MIN_SCORE;
        }else {
            return (double) (DAYS_IN_A_YEAR - daysOld) / (DAYS_IN_A_YEAR - DAYS_IN_A_WEEK);
        }
    }

    private String resolveLanguageForUser() {
        String languageCode = acceptLanguageDisplayService.resolveLanguage();
        return Language.fromCode(languageCode).getCode();
    }

    private User fetchUser(Long userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private List<EcoNews> fetchAIGeneratedNews() {
        return ecoNewsRepo.findAllAIGeneratedSince(
            AI_USER_EMAIL, ZonedDateTime.now().minusDays(DAYS_IN_A_YEAR));
    }

    private List<String> fetchUserHabitNames(Long userId) {
        return habitAssignRepo.fetchHabitNamesByUserId(userId);
    }

    private List<EcoNewsDto> mapToEcoNewsDtos(List<EcoNews> ecoNews) {
        return ecoNews.stream()
            .map(news -> modelMapper.map(news, EcoNewsDto.class))
            .toList();
    }

    private List<UserEcoNewsRelevance> computeRelevances(
        Long userId, User user,
        List<EcoNewsDto> dtos, List<String>
            habitNames, String language
    ) {
        return dtos.stream()
            .map(dto -> buildRelevance(userId, user, dto, habitNames, language))
            .toList();
    }

    private UserEcoNewsRelevance buildRelevance(Long userId, User user, EcoNewsDto dto, List<String> habitNames, String language) {
        UserEcoNewsRelevance relevance = userEcoNewsRelevanceRepo
            .findByUserIdAndEcoNewsId(userId, dto.getId())
            .orElseGet(() -> UserEcoNewsRelevance.builder().build());
        double score = calculateRelevanceScore(dto, habitNames, language);
        EcoNews ecoNewsEntity = modelMapper.map(dto, EcoNews.class);
        UserEcoNewsRelevance.builder()
            .user(user)
            .ecoNews(ecoNewsEntity)
            .relevance(score)
            .build();
        return relevance;
    }

    private List<EcoNewsDto> prepareAIGeneratedNewsDtos() {
        List<EcoNews> aiGeneratedNews = fetchAIGeneratedNews();
        return mapToEcoNewsDtos(aiGeneratedNews);
    }

    private void saveRelevances(List<UserEcoNewsRelevance> relevances) {
        userEcoNewsRelevanceRepo.saveAll(relevances);
        userEcoNewsRelevanceRepo.flush();
    }
}
