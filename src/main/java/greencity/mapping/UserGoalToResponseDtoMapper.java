package greencity.mapping;

import static greencity.constant.ErrorMessage.CUSTOM_GOAL_NOT_FOUND_BY_ID;

import greencity.constant.ErrorMessage;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.CustomGoal;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.exception.exceptions.GoalNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserGoalToResponseDtoMapper implements MapperToDto<UserGoal, UserGoalResponseDto> {
    private ModelMapper modelMapper;
    private GoalRepo goalRepo;
    private CustomGoalRepo customGoalRepo;

    @Override
    public UserGoalResponseDto convertToDto(UserGoal entity) {
        UserGoalResponseDto dto = modelMapper.map(entity, UserGoalResponseDto.class);
        if (entity.getCustomGoal() == null) {
            Goal goal = goalRepo
                .findById(entity
                    .getGoal().getId()).orElseThrow(() -> new GoalNotFoundException(ErrorMessage.GOAL_NOT_FOUND_BY_ID));
            dto.setText(goal.getText());
        } else if (entity.getGoal() == null) {
            CustomGoal customGoal = customGoalRepo.findById(entity
                .getCustomGoal().getId()).orElseThrow(() -> new NotFoundException(CUSTOM_GOAL_NOT_FOUND_BY_ID));
            dto.setText(customGoal.getText());
        }
        return dto;
    }
}
