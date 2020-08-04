package greencity.entity;

import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime enrollDate;

    @ManyToOne
    private HabitStatus habitStatus;

    /**
     * Constructor without id.
     * @param enrollDate - enroll date for {@link HabitStatusCalendar}
     * @param habitStatus - habit status for {@link HabitStatusCalendar}
     */
    public HabitStatusCalendar(LocalDateTime enrollDate, HabitStatus habitStatus) {
        this.enrollDate = enrollDate;
        this.habitStatus = habitStatus;
    }
}
