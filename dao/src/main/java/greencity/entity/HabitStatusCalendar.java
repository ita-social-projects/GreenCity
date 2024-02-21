package greencity.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@Builder
@Table(name = "habit_status_calendar")
@Entity
public class HabitStatusCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enroll_date")
    private LocalDate enrollDate;

    @ManyToOne
    private HabitAssign habitAssign;
}
