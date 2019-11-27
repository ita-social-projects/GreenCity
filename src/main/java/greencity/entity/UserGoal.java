package greencity.entity;

import greencity.entity.enums.GoalStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@Table(name = "user_goals")
@NoArgsConstructor
@AllArgsConstructor
@ToString(
    exclude = {"goal", "user"})
public class UserGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Goal goal;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private GoalStatus status = GoalStatus.ACTIVE;

    @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss.zzz")
    private LocalDateTime dateCompleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserGoal userGoal = (UserGoal) o;
        return Objects.equals(user.getId(), userGoal.user.getId())
            && Objects.equals(goal.getId(), userGoal.goal.getId())
            && status == userGoal.status
            && Objects.equals(dateCompleted, userGoal.dateCompleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), goal.getId(), status, dateCompleted);
    }
}
