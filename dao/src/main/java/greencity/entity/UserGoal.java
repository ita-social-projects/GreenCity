package greencity.entity;

import greencity.entity.enums.GoalStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "user_goals")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
