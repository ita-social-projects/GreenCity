package greencity.dto.user;

import greencity.dto.goal.CustomGoalVO;
import greencity.dto.goal.GoalVO;
import greencity.enums.GoalStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = {"user"})
@Builder
public class UserGoalVO {
    private Long id;

    private UserVO user;

    private GoalVO goal;

    private CustomGoalVO customGoal;

    private GoalStatus status = GoalStatus.ACTIVE;

    private LocalDateTime dateCompleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGoalVO)) {
            return false;
        }
        UserGoalVO userGoal = (UserGoalVO) o;
        return id.equals(userGoal.id)
            && user.equals(userGoal.user)
            && Objects.equals(goal, userGoal.goal)
            && Objects.equals(customGoal, userGoal.customGoal)
            && status == userGoal.status
            && Objects.equals(dateCompleted, userGoal.dateCompleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, goal, customGoal, status, dateCompleted);
    }
}
