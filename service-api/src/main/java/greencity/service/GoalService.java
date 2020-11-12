package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.goal.*;
import greencity.dto.language.LanguageTranslationDTO;


import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
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
    List<LanguageTranslationDTO> saveGoal(GoalManagementDto goalPostDto);

    /**
     * Method to update goal translations from {@link GoalPostDto}.
     *
     * @param goalPostDto new text
     * @author Dmytro Khonko
     */
    List<LanguageTranslationDTO> update(GoalManagementDto goalPostDto);

    /**
     * Method delete goal.
     *
     * @param goalId id of goal you need to delete
     * @author Dmytro Khonko
     */
    void delete(Long goalId);

    /**
     * Method delete goal.
     *
     * @author Dmytro Khonko
     */
    public PageableAdvancedDto<GoalManagementDto> findGoalForManagementByPage(Pageable pageable);

    /**
     * Method delete goal.
     *
     * @author Dmytro Khonko
     */
    public PageableAdvancedDto<GoalManagementDto> searchBy(Pageable paging, String query);
}
