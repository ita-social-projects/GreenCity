package greencity.service;

import greencity.dto.goal.GoalDto;
import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.CustomGoal;
import greencity.entity.Goal;
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

    /**
     * Method for getting {@link UserGoalResponseDto} from {@link UserGoal} if {@link Goal} is predefined.
     *
     * @param userGoal needed text from GoalTranslation
     * @return userGoalResponseDto.
     */
    UserGoalResponseDto getUserGoalResponseDtoFromPredefinedGoal(UserGoal userGoal);

    /**
     * Method for getting {@link UserGoalResponseDto} from {@link UserGoal} if there was set a {@link CustomGoal}.
     *
     * @param userGoal needed text from CustomGoal
     * @return userGoalResponseDto.
     */
    UserGoalResponseDto getUserGoalResponseDtoFromCustomGoal(UserGoal userGoal);

    /**
     * Method returns shopping list by user id.
     *
     * @return shopping list {@link ShoppingListDtoResponse}.
     * @author Marian Datsko
     */
    List<ShoppingListDtoResponse> getShoppingList(Long userId, String languageCode);

    /**
     * Method change goal or custom goal status.
     *
     * @author Marian Datsko
     */
    void changeGoalOrCustomGoalStatus(Long userId, Boolean status, Long goalId, Long customGoalId);
}
