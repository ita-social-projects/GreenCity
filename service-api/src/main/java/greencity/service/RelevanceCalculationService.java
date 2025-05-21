package greencity.service;

import greencity.dto.econews.EcoNewsDto;
import java.util.List;

public interface RelevanceCalculationService {
    double calculateRelevanceScore(EcoNewsDto news, List<String> habits, String language);
    double calculateAIRelevanceScore(EcoNewsDto news, List<String> habits, List<String> tags);
    List<Double> findScoresByNewsId(Long newsId);
}
