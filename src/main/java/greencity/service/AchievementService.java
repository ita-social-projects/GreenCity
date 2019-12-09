package greencity.service;

import greencity.dto.achievement.AchievementDTO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AchievementService {
    /**
     * dasd.
     * @return das.
     */
    List<AchievementDTO> findAll();
}
