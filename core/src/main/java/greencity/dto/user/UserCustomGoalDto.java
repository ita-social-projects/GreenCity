package greencity.dto.user;

import greencity.dto.goal.CustomGoalRequestDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserCustomGoalDto {
    private CustomGoalRequestDto customGoal;
}
