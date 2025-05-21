package greencity.service;

import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.UserEcoNewsRelevanceResponseDto;
import java.util.List;

public interface RelevancePersistenceService {
    void updateRelevanceForUser(Long userId, EcoNewsDto ecoNews, double score, double minScore, double maxScore);
    List<UserEcoNewsRelevanceResponseDto> getRelevantNewsForUser(Long userId);
    List<Double> findScoresByNewsId(Long ecoNewsId);
}
