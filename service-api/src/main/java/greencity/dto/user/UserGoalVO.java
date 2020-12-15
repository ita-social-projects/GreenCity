package greencity.dto.user;

import greencity.dto.goal.GoalVO;
import greencity.dto.habit.HabitAssignVO;
import greencity.enums.GoalStatus;
import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class UserGoalVO {
    private Long id;

    private HabitAssignVO habitAssign;

    private GoalVO goal;

    private GoalStatus status = GoalStatus.ACTIVE;

    private LocalDateTime dateCompleted;
}
