package greencity.service;

import static greencity.constant.UserEcoNewsRelevanceConstants.*;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.UserEcoNewsRelevanceResponseDto;
import greencity.entity.EcoNews;
import greencity.enums.Language;
import greencity.repository.EcoNewsRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserEcoNewsRelevanceRepo;
import java.time.ZonedDateTime;
import java.util.*;
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
        Set<String> tagNames = contentAnalyzer.extractTags(ecoNews);
        Set<String> habitSet = contentAnalyzer.toLowerCaseSet(habitNames);
        Set<String> contentKeywords = contentAnalyzer.extractContentKeywords(ecoNews);

        int totalMatches = similarityCalculator.countMatches(contentKeywords, habitSet);
        double tagScore = similarityCalculator.calculateJaccardSimilarity(tagNames, habitSet);
        double keywordScore = similarityCalculator.calculateKeywordOverlap(contentKeywords, habitSet);
        double semanticScore = semanticScoreCalculator.calculateSemanticScore(
            contentKeywords, habitSet, minScore, maxScore);
        double recencyScore = recencyScoreCalculator.calculateRecencyScore(
            ecoNews.getCreationDate(), minScore, maxScore);
        double algoScore = scoreCombiner.calculateFinalScore(
            tagScore, keywordScore, semanticScore, recencyScore, minScore, maxScore);
        double confidence = confidenceEstimator.estimateConfidence(
            tagScore, keywordScore, semanticScore, contentKeywords.size(), totalMatches);

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
}

