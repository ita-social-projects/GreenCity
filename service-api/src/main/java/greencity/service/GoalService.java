package greencity.service;

import greencity.dto.goal.*;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserGoalVO;

import java.util.List;

public interface GoalService {
    /**
     * Method returns list of goals, available for tracking for specific language.
     *
     * @param language needed language code
     * @return List of {@link GoalDto}.
     */
    List<GoalDto> findAll(String language);

    /**
     * Method for getting {@link UserGoalResponseDto} from {@link UserGoalVO}.
     *
     * @param userGoal needed text from GoalTranslation
     * @return userGoalResponseDto.
     */
    UserGoalResponseDto getUserGoalResponseDtoFromPredefinedGoal(UserGoalVO userGoal);

    /**
     * Method save goal.
     *
     * @author Dmytro Khonko
     */
    List<GoalTranslationVO> saveGoal(GoalPostDto goal);

    /**
     * Method to update goal .
     *
     * @author Dmytro Khonko
     */
    List<GoalTranslationVO> update(GoalPostDto dto, Long goalId);

    /**
     * Method delete goal.
     *
     * @author Dmytro Khonko
     */
    void delete(Long goalId);
}
