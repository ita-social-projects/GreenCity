package greencity.dto.user;

import greencity.dto.goal.GoalDto;
import greencity.entity.enums.GoalStatus;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserGoalDto {
    @NotNull
    @Min(value = 1, message = "UserGoal id must be a positive number")
    Long id;

    @Valid
    @NotNull(message = "Goal must not be null")
    private GoalDto goal;

    @NotNull(message = "UserGoal status must not be null")
    private GoalStatus status;
}
