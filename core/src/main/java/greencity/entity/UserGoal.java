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
@ToString(exclude = {"user"})
@Builder
public class UserGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    private CustomGoal customGoal;

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
        if (!(o instanceof UserGoal)) {
            return false;
        }
        UserGoal userGoal = (UserGoal) o;
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
