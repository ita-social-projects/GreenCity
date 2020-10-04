package greencity.entity;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "habit_status")
@Entity
public class HabitStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "working_days")
    private Integer workingDays;

    @Column(name = "habit_streak")
    private Integer habitStreak;

    @Column(name = "last_enrollment")
    private LocalDateTime lastEnrollmentDate;

    @OneToOne
    private HabitAssign habitAssign;

    @OneToMany(mappedBy = "habitStatus", cascade = CascadeType.ALL)
    private List<HabitStatusCalendar> habitStatusCalendars;
}