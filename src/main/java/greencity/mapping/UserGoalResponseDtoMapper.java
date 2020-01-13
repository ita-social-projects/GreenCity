package greencity.mapping;

import static greencity.constant.ErrorMessage.*;

import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.CustomGoal;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.exception.exceptions.GoalNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.service.LanguageService;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link org.modelmapper.ModelMapper} to map {@link UserGoal} into {@link UserGoalResponseDto}.
 */
@Component
public class UserGoalResponseDtoMapper extends AbstractConverter<UserGoal, UserGoalResponseDto> {
    private final LanguageService languageService;
    private final GoalRepo goalRepo;
    private final GoalTranslationRepo goalTranslationRepo;
    private final CustomGoalRepo customGoalRepo;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public UserGoalResponseDtoMapper(LanguageService languageService, GoalRepo goalRepo,
                                     GoalTranslationRepo goalTranslationRepo, CustomGoalRepo customGoalRepo) {
        this.languageService = languageService;
        this.goalRepo = goalRepo;
        this.goalTranslationRepo = goalTranslationRepo;
        this.customGoalRepo = customGoalRepo;
    }

    /**
     * Method for converting {@link UserGoal} into {@link UserGoalResponseDto}.
     *
     * @param userGoal object to convert.
     * @return converted object.
     */
    @Override
    protected UserGoalResponseDto convert(UserGoal userGoal) {
        UserGoalResponseDto dto = new UserGoalResponseDto();
        String languageCode = languageService.extractLanguageCodeFromRequest();

        dto.setId(userGoal.getId());
        dto.setStatus(userGoal.getStatus());

        if (userGoal.getCustomGoal() == null) {
            Goal goal = goalRepo
                .findById(userGoal
                    .getGoal().getId()).orElseThrow(() -> new GoalNotFoundException(GOAL_NOT_FOUND_BY_ID));
            dto.setText(goalTranslationRepo.findByGoalAndLanguageCode(goal, languageCode)
                .orElseThrow(() -> new GoalNotFoundException(GOAL_NOT_FOUND_BY_LANGUAGE_CODE)).getText());
        } else if (userGoal.getGoal() == null) {
            CustomGoal customGoal = customGoalRepo.findById(userGoal
                .getCustomGoal().getId()).orElseThrow(() -> new NotFoundException(CUSTOM_GOAL_NOT_FOUND_BY_ID));
            dto.setText(customGoal.getText());
        }
        return dto;
    }
}
