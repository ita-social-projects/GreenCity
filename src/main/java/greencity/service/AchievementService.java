package greencity.service;

import greencity.dto.achievement.AchievementDTO;
import greencity.entity.Achievement;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AchievementService {
    /**
     * Method for finding all the achievements.
     *
     * @return list of all{@link Achievement}.
     */
    List<AchievementDTO> findAll();
}
