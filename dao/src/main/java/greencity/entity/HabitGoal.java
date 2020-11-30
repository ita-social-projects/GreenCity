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
@EqualsAndHashCode
@Builder
public class HabitGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Habit habit;

    @ManyToOne(fetch = FetchType.LAZY)
    private Goal goal;

}
