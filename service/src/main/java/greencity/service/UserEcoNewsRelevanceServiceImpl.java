package greencity.service;

import static greencity.constant.UserEcoNewsRelevanceConstants.*;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.UserEcoNewsRelevanceResponseDto;
import greencity.entity.EcoNews;
import greencity.enums.Language;
import greencity.repository.EcoNewsRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserEcoNewsRelevanceRepo;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEcoNewsRelevanceServiceImpl implements UserEcoNewsRelevanceService {
    @Value("${min.score}")
    private double minScore;
    @Value("${max.score}")
    private double maxScore;
    private final UserEcoNewsRelevanceRepo userEcoNewsRelevanceRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final AcceptLanguageDisplayService acceptLanguageDisplayService;
    private final AIEcoNewsRelevanceService aiEcoNewsRelevanceService;
    private final RelevancePersistenceService relevancePersistenceService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void calculateRelevanceForAIGeneratedNews(Long userId) {
        String language = resolveLanguageForUser();
        List<EcoNewsDto> aiGeneratedNewsDtos = prepareAIGeneratedNewsDtos();
        List<String> habitNames = fetchUserHabitNames(userId);

        for (EcoNewsDto dto : aiGeneratedNewsDtos) {
            double score = calculateRelevanceScore(dto, habitNames, language);
            relevancePersistenceService.updateRelevanceForUser(userId, dto, score, minScore, maxScore);
        }
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
            return minScore;
        }

        Set<String> tagNames = extractTags(ecoNews);
        Set<String> habitSet = toLowerCaseSet(habitNames);
        Set<String> contentKeywords = extractContentKeywords(ecoNews);

        int totalMatches = countMatches(contentKeywords, habitSet);
        double tagScore = calculateJaccardSimilarity(tagNames, habitSet);
        double keywordScore = calculateKeywordOverlap(contentKeywords, habitSet);
        double semanticScore = calculateSemanticScore(contentKeywords, habitSet);
        double recencyScore = calculateRecencyScore(ecoNews.getCreationDate());
        double algoScore = calculateFinalScore(tagScore, keywordScore, semanticScore, recencyScore);
        double confidence = estimateConfidence(tagScore, keywordScore, semanticScore, contentKeywords.size(), totalMatches);

        if (algoScore < FALLBACK_THRESHOLD || confidence < 0.5) {
            return aiEcoNewsRelevanceService.calculateAIRelevanceScore(ecoNews, habitNames, ecoNews.getTags());
        }
        return algoScore;
    }

    @Override
    @Transactional
    public void precomputeRelevanceForNewNews(EcoNewsDto ecoNews) {
        aiEcoNewsRelevanceService.precomputeRelevanceForNewNews(ecoNews);
    }

    @Override
    @Transactional
    public void recalculateRelevanceForUser(Long userId) {
        aiEcoNewsRelevanceService.recalculateRelevanceForUser(userId);
    }

    @Override
    public List<Double> findScoresByNewsId(Long ecoNewsId) {
        return userEcoNewsRelevanceRepo.findRelevanceScoresByEcoNewsId(ecoNewsId);
    }

    private double calculateSemanticScore(Set<String> contentWords, Set<String> habitNames) {
        if (contentWords.isEmpty() || habitNames.isEmpty()) {
            return minScore;
        }

        List<String> contentList = new ArrayList<>(contentWords);
        List<String> habitList = new ArrayList<>(habitNames);
        int totalPairs = contentList.size() * habitList.size();
        if (totalPairs == 0) {
            return minScore;
        }

        SemanticScoreResult result = computeSemanticScores(contentList, habitList);
        double normalizedScore = result.totalScore / totalPairs;

        if (result.strongMatches == 0 && result.totalScore > 0) {
            normalizedScore *= 0.7;
        }

        double sizeWeight = 0.7 + 0.3 * Math.min(1.0, Math.max(contentList.size(), habitList.size()) / 30.0);
        double finalScore = normalizedScore * sizeWeight;
        return clamp(finalScore, minScore, maxScore);
    }

    private SemanticScoreResult computeSemanticScores(List<String> contentList, List<String> habitList) {
        double totalScore = 0.0;
        int strongMatches = 0;

        for (String contentWord : contentList) {
            for (String habitWord : habitList) {
                double score = calculateWordSimilarity(contentWord, habitWord);
                totalScore += score;
                if (score > 0.7) {
                    strongMatches++;
                }
            }
        }

        return new SemanticScoreResult(totalScore, strongMatches);
    }

    private double calculateWordSimilarity(String contentWord, String habitWord) {
        if (contentWord.equals(habitWord)) {
            return 1.0;
        }

        int contentLength = contentWord.length();
        int habitLength = habitWord.length();
        int minLength = Math.min(contentLength, habitLength);
        int maxLength = Math.max(contentLength, habitLength);
        double score = 0.0;

        double prefixSuffixScore = calculatePrefixSuffixSimilarity(
            contentWord, habitWord, minLength, maxLength);
        score = Math.max(score, prefixSuffixScore);
        int levenshteinDistance = calculateLevenshteinDistance(contentWord, habitWord);
        double levenshteinSimilarity = 1.0 - (levenshteinDistance / (double) maxLength);
        if (levenshteinSimilarity > 0.7) {
            score = Math.max(score, levenshteinSimilarity * 0.8);
        }

        double ngramSimilarity = calculateNGramsSimilarity(contentWord, habitWord, 3);
        score = Math.max(score, ngramSimilarity * 0.5);
        double lengthWeight = 0.5 + Math.min(0.5, maxLength / 20.0);
        return score * lengthWeight;
    }

    private double calculatePrefixSuffixSimilarity(String contentWord, String habitWord, int minLength, int maxLength) {
        double score = 0.0;
        double lengthRatio = (double) minLength / maxLength;

        if (contentWord.startsWith(habitWord) || habitWord.startsWith(contentWord)) {
            score = Math.max(score, 0.7 * lengthRatio);
        }
        if (contentWord.endsWith(habitWord) || habitWord.endsWith(contentWord)) {
            score = Math.max(score, 0.6 * lengthRatio);
        }

        return score;
    }

    private record SemanticScoreResult(double totalScore, int strongMatches) {
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

    private double calculateFinalScore(double tagScore, double keywordScore, double semanticScore, double recencyScore) {
        double finalScore = TAG_SCORE_WEIGHT * tagScore
            + KEYWORD_SCORE_WEIGHT * keywordScore
            + SEMANTIC_SCORE_WEIGHT * semanticScore
            + RECENCY_SCORE_WEIGHT * recencyScore;
        return clamp(finalScore, minScore, maxScore);
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() || set2.isEmpty()) {
            return minScore;
        }
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    private double calculateKeywordOverlap(Set<String> contentWords, Set<String> habitNames) {
        if (contentWords.isEmpty() || habitNames.isEmpty()) {
            return minScore;
        }
        long matches = contentWords.stream()
            .filter(habitNames::contains)
            .count();
        return (double) matches / Math.max(contentWords.size(), habitNames.size());
    }

    private double calculateRecencyScore(ZonedDateTime creationDate) {
        if (creationDate == null) {
            return minScore;
        }
        long daysOld = Duration.between(creationDate, ZonedDateTime.now()).toDays();
        if (daysOld <= DAYS_IN_A_WEEK) {
            return maxScore;
        } else if (daysOld >= DAYS_IN_A_YEAR) {
            return minScore;
        } else {
            double decayRate = -Math.log(0.1) / (DAYS_IN_A_YEAR - DAYS_IN_A_WEEK);
            return maxScore * Math.exp(-decayRate * (daysOld - DAYS_IN_A_WEEK));
        }
    }

    private String resolveLanguageForUser() {
        String languageCode = acceptLanguageDisplayService.resolveLanguage();
        return Language.fromCode(languageCode).getCode();
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

    private List<EcoNewsDto> prepareAIGeneratedNewsDtos() {
        List<EcoNews> aiGeneratedNews = fetchAIGeneratedNews();
        return mapToEcoNewsDtos(aiGeneratedNews);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }


    private double estimateConfidence(double tagScore, double keywordScore, double semanticScore,
                                      int contentSize, int totalMatches) {
        double[] scores = {tagScore, keywordScore, semanticScore};
        double avgScore = Arrays.stream(scores).average().orElse(0.0);
        double weightedVariance = 0.0;
        double[] weights = {0.25, 0.35, 0.4};

        for (int i = 0; i < scores.length; i++) {
            weightedVariance += weights[i] * Math.pow(scores[i] - avgScore, 2);
        }

        double consistencyScore = Math.max(0.0, 1.0 - Math.sqrt(weightedVariance) * 2.0);
        double contentSizeScore;
        if (contentSize < 5) {
            contentSizeScore = 0.5;
        } else if (contentSize < 15) {
            contentSizeScore = 0.7 + (contentSize - 5) * 0.02;
        } else if (contentSize < 50) {
            contentSizeScore = 0.9 + (contentSize - 15) * 0.00286;
        } else {
            contentSizeScore = 1.0;
        }

        double matchQualityScore;
        if (totalMatches == 0) {
            matchQualityScore = 0.3;
        } else {
            double matchRatio = Math.min(1.0, (double) totalMatches / contentSize);
            matchQualityScore = 0.5 + 0.5 * matchRatio;
        }

        boolean hasOutlier = false;
        for (double score : scores) {
            if (Math.abs(score - avgScore) > 0.5) {
                hasOutlier = true;
                break;
            }
        }

        double outlierPenalty = hasOutlier ? 0.8 : 1.0;
        double highScoreBonus = avgScore > 0.7 ? 1.1 : 1.0;
        double confidenceScore = (consistencyScore * 0.4 +
            contentSizeScore * 0.3 +
            matchQualityScore * 0.3) *
            outlierPenalty *
            highScoreBonus;

        if (log.isDebugEnabled()) {
            log.debug("Confidence calculation: consistencyScore={}, contentSizeScore={}, matchQualityScore={}, " +
                    "outlierPenalty={}, highScoreBonus={}, final={}",
                consistencyScore, contentSizeScore, matchQualityScore,
                outlierPenalty, highScoreBonus, confidenceScore);
        }

        return clamp(confidenceScore, 0.0, 1.0);
    }

    private int calculateLevenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    private double calculateSimilarity(String s1, String s2) {
        int distance = calculateLevenshteinDistance(s1.toLowerCase(), s2.toLowerCase());
        int maxLength = Math.max(s1.length(), s2.length());
        return maxLength > 0 ? 1.0 - (double) distance / maxLength : 1.0;
    }

    private int countMatches(Set<String> set1, Set<String> set2) {
        int matches = 0;
        for (String item1 : set1) {
            for (String item2 : set2) {
                if (item1.equals(item2) || calculateSimilarity(item1, item2) > RELEVANCE_THRESHOLD) {
                    matches++;
                    break;
                }
            }
        }
        return matches;
    }

    @SuppressWarnings("SameParameterValue")
    private double calculateNGramsSimilarity(String s1, String s2, int n) {
        if (s1.length() < n || s2.length() < n) {
            return 0.0;
        }

        Set<String> ngrams1 = generateNGrams(s1, n);
        Set<String> ngrams2 = generateNGrams(s2, n);
        Set<String> intersection = new HashSet<>(ngrams1);
        intersection.retainAll(ngrams2);

        Set<String> union = new HashSet<>(ngrams1);
        union.addAll(ngrams2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private Set<String> generateNGrams(String str, int n) {
        Set<String> ngrams = new HashSet<>();
        for (int i = 0; i <= str.length() - n; i++) {
            ngrams.add(str.substring(i, i + n));
        }
        return ngrams;
    }
}
