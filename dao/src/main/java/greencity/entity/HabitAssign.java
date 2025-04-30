package greencity.entity;

import greencity.enums.HabitAssignStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.ZonedDateTime;
import java.util.List;
import org.hibernate.annotations.BatchSize;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
@Table(name = "habit_assign")
public class HabitAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private HabitAssignStatus status;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "working_days", nullable = false)
    private Integer workingDays;

    @Column(name = "habit_streak", nullable = false)
    private Integer habitStreak;

    @Column(name = "last_enrollment", nullable = false)
    private ZonedDateTime lastEnrollmentDate;

    /**
     * This variable shows that the progress notification has displayed and habit
     * has enough progress (from 80 to 100 %) to be in status ACQUIRED. Now user can
     * change status from INPROGRESS to ACQUIRED or continue to enroll habit to
     * 100%.
     *
     */
    @Column(name = "progress_notification_has_displayed", nullable = false)
    private Boolean progressNotificationHasDisplayed;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @OneToMany(mappedBy = "habitAssign", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    private List<UserToDoListItem> userToDoListItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habit habit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "habitAssign", cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    private List<HabitStatistic> habitStatistic;

    @OneToMany(mappedBy = "habitAssign", cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    private List<HabitStatusCalendar> habitStatusCalendars;

    @OneToMany(mappedBy = "inviterHabitAssign", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<HabitInvitation> invitationsSent;

    @OneToMany(mappedBy = "inviteeHabitAssign", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<HabitInvitation> invitationsReceived;
}
