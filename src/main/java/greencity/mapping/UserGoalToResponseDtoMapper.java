package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.exception.exceptions.GoalNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.GoalRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserGoalToResponseDtoMapper implements MapperToDto<UserGoal, UserGoalResponseDto> {
    private ModelMapper modelMapper;
    private GoalRepo goalRepo;

    @Override
    public UserGoalResponseDto convertToDto(UserGoal entity) {
        UserGoalResponseDto dto = modelMapper.map(entity, UserGoalResponseDto.class);
        Goal goal = goalRepo
            .findById(entity
                .getGoal().getId()).orElseThrow(() -> new GoalNotFoundException(ErrorMessage.GOAL_NOT_FOUND_BY_ID));
        dto.setText(goal.getText());
        return dto;
    }
}
