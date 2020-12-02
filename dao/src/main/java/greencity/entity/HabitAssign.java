package greencity.entity;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "habit_assign")
public class HabitAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "acquired", nullable = false)
    private Boolean acquired;

    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;

    @Column(name = "suspended", nullable = false)
    private Boolean suspended;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "working_days", nullable = false)
    private Integer workingDays;

    @Column(name = "habit_streak", nullable = false)
    private Integer habitStreak;

    @Column(name = "last_enrollment", nullable = false)
    private ZonedDateTime lastEnrollmentDate;

    @OneToMany(mappedBy = "habitAssign", fetch = FetchType.LAZY)
    private List<UserGoal> userGoals = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Habit habit;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "habitAssign", cascade = CascadeType.ALL)
    private List<HabitStatistic> habitStatistic;

    @OneToMany(mappedBy = "habitAssign", cascade = CascadeType.ALL)
    private List<HabitStatusCalendar> habitStatusCalendars;
}
