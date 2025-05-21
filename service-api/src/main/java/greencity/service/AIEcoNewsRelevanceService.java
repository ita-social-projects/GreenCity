package greencity.service;

import greencity.dto.econews.EcoNewsDto;
import greencity.exception.exceptions.OpenAIServiceException;
import java.util.List;

public interface AIEcoNewsRelevanceService {
    double calculateAIRelevanceScore(EcoNewsDto ecoNews, List<String> habitNames, List<String> tags);
    void precomputeRelevanceForNewNews(EcoNewsDto ecoNews);
    void recalculateRelevanceForUser(Long userId);
    double recover(OpenAIServiceException e, EcoNewsDto ecoNews, List<String> habitNames, List<String> tags);
}
