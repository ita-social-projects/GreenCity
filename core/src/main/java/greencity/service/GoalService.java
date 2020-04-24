package greencity.service;

import greencity.dto.goal.GoalDto;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.Habit;
import greencity.entity.UserGoal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface GoalService {
    /**
     * Method returns list of goals, available for tracking for specific language.
     *
     * @param language needed language code
     * @return List of {@link GoalDto}.
     */
    List<GoalDto> findAll(String language);

    UserGoalResponseDto getUserGoalResponseDto(UserGoal userGoal);
}
