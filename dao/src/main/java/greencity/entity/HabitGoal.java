package greencity.entity;

import greencity.enums.GoalStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "habit_goals")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = {"user"})
@Builder
public class HabitGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Habit habit;

    @ManyToOne(fetch = FetchType.LAZY)
    private Goal goal;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HabitGoal)) {
            return false;
        }
        HabitGoal habitGoal = (HabitGoal) o;
        return id.equals(habitGoal.id)
            && habit.equals(habitGoal.habit)
            && Objects.equals(goal, habitGoal.goal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, habit, goal);
    }
}
