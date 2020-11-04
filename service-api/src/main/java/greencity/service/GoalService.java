package greencity.service;

import greencity.dto.goal.GoalDto;
import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.dto.goal.CustomGoalVO;
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
     * Method for getting {@link UserGoalResponseDto} from {@link UserGoalVO} if
     * there was set a {@link CustomGoalVO}.
     *
     * @param userGoal needed text from CustomGoal
     * @return userGoalResponseDto.
     */
    UserGoalResponseDto getUserGoalResponseDtoFromCustomGoal(UserGoalVO userGoal);

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
    void changeGoalOrCustomGoalStatus(Long userId, boolean status, Long goalId, Long customGoalId);
}
