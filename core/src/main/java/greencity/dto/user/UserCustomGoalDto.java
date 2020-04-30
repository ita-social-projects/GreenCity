package greencity.dto.user;

import greencity.dto.goal.CustomGoalRequestDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserCustomGoalDto {
    private CustomGoalRequestDto customGoal;
}
