package greencity.service;

import com.github.benmanes.caffeine.cache.*;
import static greencity.constant.UserEcoNewsRelevanceConstants.*;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.UserEcoNewsRelevanceResponseDto;
import greencity.entity.EcoNews;
import greencity.entity.cache.CachedAINews;
import greencity.entity.cache.CachedUserHabits;
import greencity.enums.Language;
import greencity.model.RelevanceComponents;
import greencity.repository.EcoNewsRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserEcoNewsRelevanceRepo;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    private final EcoNewsContentAnalyzerService contentAnalyzer;
    private final RelevanceConfidenceEstimatorService confidenceEstimator;
    private final RecencyScoreCalculatorService recencyScoreCalculator;
    private final RelevanceScoreCombinerService scoreCombiner;
    private final SimilarityMetricsCalculatorService similarityCalculator;
    private final SemanticScoreCalculatorService semanticScoreCalculator;
    private final ModelMapper modelMapper;

    private static final Weigher<Long, CachedUserHabits> USER_HABITS_WEIGHER =
        (key, value) -> value.habitNames().size() + value.habitSet().size();
    private static final Weigher<String, CachedAINews> AI_NEWS_WEIGHER =
        (key, value) -> value.newsList().size();

    private final AsyncLoadingCache<Long, CachedUserHabits> userHabitsCache = Caffeine.newBuilder()
        .refreshAfterWrite(USER_HABITS_REFRESH_INTERVAL)
        .expireAfterWrite(USER_HABITS_CACHE_EXPIRY)
        .maximumWeight(USER_HABITS_CACHE_MAX_WEIGHT)
        .weigher(USER_HABITS_WEIGHER)
        .recordStats()
        .buildAsync((key, executor) -> fetchUserHabitsFromRepoAsync(key));

    private final AsyncLoadingCache<String, CachedAINews> aiNewsCache = Caffeine.newBuilder()
        .refreshAfterWrite(AI_NEWS_REFRESH_INTERVAL)
        .expireAfterWrite(AI_NEWS_CACHE_EXPIRY)
        .maximumWeight(AI_NEWS_CACHE_MAX_WEIGHT)
        .weigher(AI_NEWS_WEIGHER)
        .recordStats()
        .buildAsync((key, executor) -> fetchCachedAINewsAsync(key));

    @Override
    @Transactional
    public void calculateRelevanceForAIGeneratedNews(Long userId) {
        String language = resolveLanguageForUser();
        String targetLanguage = UK_LANGUAGE_CODE.equals(language) ? UK_LANGUAGE_CODE : language;
        List<EcoNewsDto> aiGeneratedNewsDtos = prepareAIGeneratedNewsDtos(targetLanguage);
        CachedUserHabits cachedHabits = userHabitsCache.get(userId).join();
        if (aiGeneratedNewsDtos.size() > ASYNC_THRESHOLD) {
            processNewsInParallel(userId, aiGeneratedNewsDtos, cachedHabits, language).join();
        } else {
            processNewsSequentially(userId, aiGeneratedNewsDtos, cachedHabits, language);
        }
    }

    @Override
    public List<UserEcoNewsRelevanceResponseDto> getRelevantNewsForUser(Long userId) {
        return userEcoNewsRelevanceRepo
            .findByUserIdAndAIGeneratedNews(userId, AI_USER_EMAIL, RELEVANCE_THRESHOLD)
            .stream()
            .map(relevance -> new UserEcoNewsRelevanceResponseDto(
                relevance.getId(),
                relevance.getEcoNews().getId(),
                relevance.getRelevance()))
            .toList();
    }

    @Override
    public double calculateRelevanceScore(EcoNewsDto ecoNews, List<String> habitNames, String language) {
        Set<String> habitSet = contentAnalyzer.toLowerCaseSet(habitNames);
        return calculateRelevanceScore(ecoNews, habitNames, habitSet, language);
    }

    @Override
    @Transactional
    public void precomputeRelevanceForNewNews(EcoNewsDto ecoNews) {
        aiEcoNewsRelevanceService.precomputeRelevanceForNewNews(ecoNews);
    }

    @Override
    @Transactional
    public void recalculateRelevanceForUser(Long userId) {
        userHabitsCache.synchronous().invalidate(userId);
        aiEcoNewsRelevanceService.recalculateRelevanceForUser(userId);
    }

    @Override
    public List<Double> findScoresByNewsId(Long ecoNewsId) {
        return userEcoNewsRelevanceRepo.findRelevanceScoresByEcoNewsId(ecoNewsId);
    }

    private CompletableFuture<Void> processNewsInParallel(Long userId, List<EcoNewsDto> newsDtos,
                                                         CachedUserHabits cachedHabits, String language) {
        List<CompletableFuture<Void>> futures = newsDtos.stream()
            .map(dto -> CompletableFuture.runAsync(() -> {
                double score;
//                try {
                    score = calculateRelevanceScore(dto, cachedHabits.habitNames(),
                        cachedHabits.habitSet(), language);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    throw new RuntimeException("Interrupted while calculating relevance score", e);
//                } catch (ExecutionException e) {
//                    throw new RuntimeException("Error calculating relevance score", e);
//                }
                relevancePersistenceService
                    .updateRelevanceForUser(userId, dto, score, minScore, maxScore);
            }))
            .toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[EMPTY_ARRAY_SIZE]));
    }

    private void processNewsSequentially(Long userId, List<EcoNewsDto> newsDtos,
                                         CachedUserHabits cachedHabits, String language)
    {
        for (EcoNewsDto dto : newsDtos) {
            double score = calculateRelevanceScore(dto, cachedHabits.habitNames(),
                cachedHabits.habitSet(), language);
            relevancePersistenceService.updateRelevanceForUser(userId, dto, score, minScore, maxScore);
        }
    }

    private double calculateRelevanceScore(EcoNewsDto ecoNews, List<String> habitNames,
                                           Set<String> habitSet, String language) {
        if (ecoNews == null || habitNames == null || habitNames.isEmpty()) {
            return minScore;
        }

        Set<String> tagNames = contentAnalyzer.extractTags(ecoNews);
        Set<String> contentKeywords = contentAnalyzer.extractContentKeywords(ecoNews);
        RelevanceComponents components = calculateRelevanceComponents(
            contentKeywords, tagNames, habitSet, language, ecoNews.getCreationDate()
        );

        if (shouldUseAIBackup(components)) {
            return aiEcoNewsRelevanceService
                .calculateAIRelevanceScore(ecoNews, habitNames, ecoNews.getTags());
        }
        return components.algoScore();
    }


    private RelevanceComponents calculateRelevanceComponents(Set<String> contentKeywords,
                                                             Set<String> tagNames,
                                                             Set<String> habitSet,
                                                             String language,
                                                             ZonedDateTime creationDate)
    {
        int totalMatches = similarityCalculator.countMatches(contentKeywords, habitSet, language);
        double tagScore = similarityCalculator.calculateJaccardSimilarity(tagNames, habitSet);
        double keywordScore = similarityCalculator.calculateKeywordOverlap(contentKeywords, habitSet);
        double semanticScore = semanticScoreCalculator.calculateSemanticScore(
            contentKeywords, habitSet, language, minScore, maxScore);
        double recencyScore = recencyScoreCalculator.calculateRecencyScore(creationDate, minScore, maxScore);
        double algoScore = scoreCombiner.calculateFinalScore(
            tagScore, keywordScore, semanticScore, recencyScore, minScore, maxScore);
        double confidence = confidenceEstimator.estimateConfidence(
            tagScore, keywordScore, semanticScore, contentKeywords.size(), totalMatches);

        return new RelevanceComponents(algoScore, confidence);
    }

    private boolean shouldUseAIBackup(RelevanceComponents components) {
        return components.algoScore() < FALLBACK_THRESHOLD
            || components.confidence() < MIN_CONFIDENCE_THRESHOLD;
    }

    private String resolveLanguageForUser() {
        String languageCode = acceptLanguageDisplayService.resolveLanguage();
        return Language.fromCode(languageCode).getCode();
    }

    private List<EcoNews> fetchAIGeneratedNewsFromRepo() {
        return ecoNewsRepo.findAllAIGeneratedSince(AI_USER_EMAIL,
            ZonedDateTime.now().minusDays(DAYS_IN_A_YEAR));
    }

    private CompletableFuture<CachedUserHabits> fetchUserHabitsFromRepoAsync(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> habitNames = habitAssignRepo.fetchHabitNamesByUserId(userId);
            if (habitNames.isEmpty()) {
                throw new IllegalStateException(USER_NOT_FOUND_MESSAGE + userId);
            }
            Set<String> habitSet = contentAnalyzer.toLowerCaseSet(habitNames);
            return new CachedUserHabits(habitNames, habitSet);
        });
    }

    private CompletableFuture<CachedAINews> fetchCachedAINewsAsync(String key) {
        return CompletableFuture.supplyAsync(() -> {
            String lang = key.split(UNDERSCORE)[LANGUAGE_INDEX_IN_CACHE_KEY];
            List<EcoNews> allNews = fetchAIGeneratedNewsFromRepo();
            List<EcoNews> filteredNews = allNews.stream()
                .filter(news -> UK_LANGUAGE_CODE.equals(lang) == isUkrainian(news))
                .toList();
            return new CachedAINews(mapToEcoNewsDtos(filteredNews));
        });
    }

    private List<EcoNewsDto> prepareAIGeneratedNewsDtos(String targetLanguage) {
        String cacheKey = "ai_news_" + targetLanguage;
        CachedAINews cachedAINews = aiNewsCache.get(cacheKey).join();
        return Objects.requireNonNull(cachedAINews).newsList();
    }

    private List<EcoNewsDto> mapToEcoNewsDtos(List<EcoNews> ecoNews) {
        return ecoNews.stream()
            .map(news -> modelMapper.map(news, EcoNewsDto.class))
            .toList();
    }
    private boolean isUkrainian(EcoNews news) {
        String content = news.getTitle() + EMPTY_STRING + news.getText();
        return UKRAINIAN_PATTERN.matcher(content).find();
    }
}
