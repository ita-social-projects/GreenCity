package greencity.dto.user;

import greencity.dto.goal.GoalVO;
import greencity.enums.GoalStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserGoalVO {
    private Long id;

    private UserVO user;

    private GoalVO goal;

    private GoalStatus status = GoalStatus.ACTIVE;

    private LocalDateTime dateCompleted;
}
