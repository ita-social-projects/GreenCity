package greencity.mapping;

import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.UserGoal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserGoalToResponseDtoMapper implements MapperToDto<UserGoal, UserGoalResponseDto> {
    @Override
    public UserGoalResponseDto convertToDto(UserGoal entity) {
        return null;
    }
}
