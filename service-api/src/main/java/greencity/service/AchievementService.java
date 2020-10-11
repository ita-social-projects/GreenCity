package greencity.service;

import greencity.dto.achievement.AchievementDTO;
import java.util.List;

public interface AchievementService {
    /**
     * Method for finding all the achievements.
     *
     * @return list of all{@link AchievementDTO}.
     */
    List<AchievementDTO> findAll();
}
