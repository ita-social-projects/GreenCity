package greencity.service;

import static greencity.constant.OpenAIConstants.AI_USER_EMAIL;
import static greencity.constant.UserEcoNewsRelevanceConstants.RELEVANCE_THRESHOLD;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.UserEcoNewsRelevanceResponseDto;
import greencity.entity.EcoNews;
import greencity.entity.User;
import greencity.entity.UserEcoNewsRelevance;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.UserEcoNewsRelevanceRepo;
import greencity.repository.UserRepo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RelevancePersistenceServiceImpl implements RelevancePersistenceService{
    private final UserEcoNewsRelevanceRepo userEcoNewsRelevanceRepo;
    private final UserRepo userRepo;
    private final EcoNewsRepo ecoNewsRepo;

    @Override
    @Transactional
    public void updateRelevanceForUser(Long userId, EcoNewsDto ecoNews, double score, double minScore, double maxScore) {
        if (userId == null || ecoNews == null || ecoNews.getId() == null) {
            throw new IllegalArgumentException("User ID or eco-news cannot be null");
        }
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        EcoNews ecoNewsEntity = ecoNewsRepo.findById(ecoNews.getId())
            .orElseThrow(() -> new IllegalArgumentException("EcoNews not found: " + ecoNews.getId()));
        UserEcoNewsRelevance relevance = userEcoNewsRelevanceRepo
            .findByUserIdAndEcoNewsId(userId, ecoNews.getId())
            .orElseGet(() -> UserEcoNewsRelevance.builder()
                .user(user)
                .ecoNews(ecoNewsEntity)
                .build());

        relevance.setRelevance(clamp(score, minScore, maxScore));
        userEcoNewsRelevanceRepo.save(relevance);
    }

    @Override
    public List<UserEcoNewsRelevanceResponseDto> getRelevantNewsForUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userEcoNewsRelevanceRepo.findByUserIdAndAIGeneratedNews(userId, AI_USER_EMAIL, RELEVANCE_THRESHOLD)
            .stream()
            .map(relevance -> new UserEcoNewsRelevanceResponseDto(
                relevance.getId(),
                relevance.getEcoNews().getId(),
                relevance.getRelevance()
            ))
            .toList();
    }

    @Override
    public List<Double> findScoresByNewsId(Long ecoNewsId) {
        if (ecoNewsId == null) {
            throw new IllegalArgumentException("EcoNews ID cannot be null");
        }
        return userEcoNewsRelevanceRepo.findRelevanceScoresByEcoNewsId(ecoNewsId);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
