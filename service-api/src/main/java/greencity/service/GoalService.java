package greencity.service;

import greencity.dto.goal.*;
import greencity.dto.language.LanguageTranslationDTO;
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
     * Method for saving goal from {@link GoalPostDto}.
     *
     * @param goalPostDto needed text
     * @author Dmytro Khonko
     */
    List<LanguageTranslationDTO> saveGoal(GoalPostDto goalPostDto);

    /**
     * <<<<<<< HEAD Method to update goal translations from {@link GoalPostDto}.
     * ======= Method for getting {@link UserGoalResponseDto} from
     * {@link UserGoalVO} if there was set a {@link CustomGoalVO}. >>>>>>> dev
     *
     * @param goalPostDto new text
     * @author Dmytro Khonko
     */
    List<LanguageTranslationDTO> update(GoalPostDto goalPostDto);

    /**
     * Method delete goal.
     *
     * @param goalId id of goal you need to delete
     * @author Dmytro Khonko
     */
    void delete(Long goalId);
}
