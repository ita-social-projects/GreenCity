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

    @Column(name = "suspended")
    private boolean suspended;

    @Column(name = "last_enrollment")
    private LocalDateTime lastEnrollmentDate;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @ManyToOne
    private Habit habit;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "habitStatus", cascade = CascadeType.ALL)
    private List<HabitStatusCalendar> habitStatusCalendars;
}