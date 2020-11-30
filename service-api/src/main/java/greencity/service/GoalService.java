package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.goal.*;
import greencity.dto.language.LanguageTranslationDTO;
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
}
