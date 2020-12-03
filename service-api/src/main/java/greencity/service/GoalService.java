package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.goal.*;
import greencity.dto.habit.HabitVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.UserGoalDto;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserGoalVO;
import greencity.dto.user.UserVO;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface GoalService {
    /**
     * Method returns list of goals, available for tracking for specific language.
     *
     * @param language needed language code
     * @return List of {@link GoalDto}.
     */
    List<GoalDto> findAll(String language);

    /**
     * Method for saving goal from {@link GoalPostDto}.
     *
     * @param goalPostDto needed text
     * @author Dmytro Khonko
     */
    List<LanguageTranslationDTO> saveGoal(GoalPostDto goalPostDto);

    /**
     * Method to update goal translations from {@link GoalPostDto}.
     *
     * @param goalPostDto new text
     * @author Dmytro Khonko
     */
    List<LanguageTranslationDTO> update(GoalPostDto goalPostDto);

    /**
     * Method delete goal.
     *
     * @param id id of goal you need to delete
     * @author Dmytro Khonko
     */
    Long delete(Long id);

    /**
     * Method to find goals.
     *
     * @param pageable our page
     * @author Dmytro Khonko
     */
    PageableAdvancedDto<GoalManagementDto> findGoalForManagementByPage(Pageable pageable);

    /**
     * Method search goals.
     *
     * @param paging our page.
     * @param query  search request
     * @author Dmytro Khonko
     */
    PageableAdvancedDto<GoalManagementDto> searchBy(Pageable paging, String query);

    /**
     * Method delete few goals.
     *
     * @param listId ids of goals you need to delete
     * @author Dmytro Khonko
     */
    List<Long> deleteAllGoalByListOfId(List<Long> listId);

    /**
     * Method to find goal.
     *
     * @param id id of goal you need to find
     * @author Dmytro Khonko
     */
    GoalResponseDto findGoalById(Long id);

    /**
     * Method to filter goals.
     *
     * @param goal data of goal you need to find
     * @author Dmytro Khonko
     */
    PageableAdvancedDto<GoalManagementDto> getFilteredDataForManagementByPage(Pageable pageable, GoalViewDto goal);

    /**
     * Method assign to user list of user goals available for habit.
     *
     * @param userId   id of the {@link UserVO} current user.
     * @param language needed language code.
     * @param habitId  id of the {@link HabitVO}.
     * @return List of saved {@link UserGoalDto} with specific language.
     */
    List<UserGoalResponseDto> saveUserGoals(Long userId, Long habitId, List<GoalRequestDto> dto, String language);

    /**
     * Method returns list of user goals for specific language.
     *
     * @param userId   id of the {@link UserVO} current user.
     * @param language needed language code.
     * @return List of {@link UserGoalDto}.
     */
    List<UserGoalResponseDto> getUserGoals(Long userId, Long habitId, String language);

    /**
     * Method for deleting goal from user`s shopping list.
     *
     * @param userId  id of the {@link UserVO} current user.
     * @param habitId id of the {@link HabitVO}.
     * @param goalId  id of the {@link GoalVO}.
     */
    void deleteUserGoalByGoalIdAndUserIdAndHabitId(Long goalId, Long userId, Long habitId);

    /**
     * Method update status of user goal.
     *
     * @param userId   id of the {@link UserVO} current user.
     * @param goalId   - {@link UserGoalVO}'s id that should be updated.
     * @param language needed language code.
     * @return {@link UserGoalDto} with specific language.
     */
    UserGoalResponseDto updateUserGoalStatus(Long userId, Long goalId, String language);

    /**
     * Method for deleted list of user goals.
     *
     * @param ids string with ids object for deleting.
     * @return list ids of deleted {@link UserGoalVO}
     * @author Bogdan Kuzenko
     */
    List<Long> deleteUserGoals(String ids);
}
