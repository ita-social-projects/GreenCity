package greencity.service;


import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.UserEcoNewsRelevanceResponseDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface UserEcoNewsRelevanceService {
    void calculateRelevanceForAIGeneratedNews(Long userId);
    List<UserEcoNewsRelevanceResponseDto> getRelevantNewsForUser(Long userId);
    double calculateRelevanceScore(EcoNewsDto ecoNewsDto, List<String> habitNames, String language);
    void recalculateRelevanceForUser(Long userId);
    void precomputeRelevanceForNewNews(EcoNewsDto ecoNews);
    List<Double> findScoresByNewsId(Long ecoNewsId);
}
