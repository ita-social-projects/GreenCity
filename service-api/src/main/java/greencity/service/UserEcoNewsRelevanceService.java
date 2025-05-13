package greencity.service;


import greencity.dto.econews.UserEcoNewsRelevanceResponseDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface UserEcoNewsRelevanceService {
    void calculateRelevanceForAIGeneratedNews(Long userId);
    List<UserEcoNewsRelevanceResponseDto> getRelevantNewsForUser(Long userId);
}
