package greencity.service;

import static greencity.constant.AIEcoNewsRelevanceConstants.*;
import static greencity.constant.OpenAIConstants.AI_USER_EMAIL;
import static greencity.constant.UserEcoNewsRelevanceConstants.DAYS_IN_A_YEAR;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.User;
import greencity.exception.exceptions.OpenAIServiceException;
import greencity.exception.exceptions.RelevanceBatchProcessingException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserRepo;
import io.micrometer.core.annotation.Timed;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class AIEcoNewsRelevanceServiceImpl implements AIEcoNewsRelevanceService {
    @Value("${default.score}")
    private double defaultScore;
    @Value("${min.score}")
    private double minScore;
    @Value("${max.score}")
    private double maxScore;

    private final UserRepo userRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final PlatformTransactionManager transactionManager;
    private final RelevanceCalculationService relevanceCalculationService;
    private final RelevancePersistenceService relevancePersistenceService;
    private final ModelMapper modelMapper;

    @Override
    @Timed(value = "ai_relevance_retries", description = "Number of retries for AI relevance scoring")
    @Retryable(retryFor = OpenAIServiceException.class)
    public double calculateAIRelevanceScore(EcoNewsDto ecoNews, List<String> habitNames, List<String> tags) {
        return relevanceCalculationService.calculateAIRelevanceScore(ecoNews, habitNames, tags);
    }

    @Override
    @Async
    public void precomputeRelevanceForNewNews(EcoNewsDto ecoNews) {
        List<User> users = userRepo.findAll();
        int batchSize = BATCH_SIZE;
        for (int i = DEFAULT_INDEX; i < users.size(); i += batchSize) {
            final int batchStart = i;
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(status -> {
                List<User> batch = users.subList(batchStart, Math.min(batchStart + batchSize, users.size()));
                for (User user : batch) {
                    try {
                        List<String> habitNames = habitAssignRepo.fetchHabitNamesByUserId(user.getId());
                        double score = relevanceCalculationService.calculateAIRelevanceScore(
                            ecoNews, habitNames, ecoNews.getTagsEn());
                        relevancePersistenceService.updateRelevanceForUser(user.getId(), ecoNews, score, minScore, maxScore);
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        throw new RelevanceBatchProcessingException(BATCH_PROCESSING_FAILED, e);
                    }
                }
                return null;
            });
        }
    }

    @Override
    public void recalculateRelevanceForUser(Long userId) {
        List<String> habitNames = habitAssignRepo.fetchHabitNamesByUserId(userId);
        List<EcoNewsDto> aiGeneratedNews = ecoNewsRepo.findAllAIGeneratedSince(
                AI_USER_EMAIL, ZonedDateTime.now().minusDays(DAYS_IN_A_YEAR)
            ).stream()
            .map(news -> modelMapper.map(news, EcoNewsDto.class))
            .toList();

        int batchSize = BATCH_SIZE;
        for (int i = DEFAULT_INDEX; i < aiGeneratedNews.size(); i += batchSize) {
            final int batchStart = i;
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(status -> {
                List<EcoNewsDto> batch = aiGeneratedNews.subList(batchStart,
                    Math.min(batchStart + batchSize, aiGeneratedNews.size()));
                for (EcoNewsDto ecoNews : batch) {
                    try {
                        double score = relevanceCalculationService.calculateAIRelevanceScore(
                            ecoNews, habitNames, ecoNews.getTagsEn());
                        relevancePersistenceService.updateRelevanceForUser(userId, ecoNews, score, minScore, maxScore);
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        throw new RelevanceBatchProcessingException(BATCH_PROCESSING_FAILED, e);
                    }
                }
                return null;
            });
        }
    }

    @Recover
    public double recover(OpenAIServiceException e, EcoNewsDto ecoNews, List<String> habitNames, List<String> tags) {
        List<Double> existingScores = relevancePersistenceService.findScoresByNewsId(ecoNews.getId());
        if (!existingScores.isEmpty()) {
            return existingScores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(defaultScore);
        }
        return defaultScore;
    }
}