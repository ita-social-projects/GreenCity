package greencity.dto.user;

import greencity.dto.goal.GoalRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserGoalDto {
    @Valid
    @NotNull(message = "Goal must not be null")
    private GoalRequestDto goal;
}
