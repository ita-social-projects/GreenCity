package greencity.dto.user;

import greencity.dto.goal.CustomGoalVO;
import greencity.dto.goal.GoalVO;
import greencity.enums.GoalStatus;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserGoalVO {
    private Long id;

    private UserVO user;

    private GoalVO goal;

    private CustomGoalVO customGoal;

    private GoalStatus status = GoalStatus.ACTIVE;

    private LocalDateTime dateCompleted;
}
