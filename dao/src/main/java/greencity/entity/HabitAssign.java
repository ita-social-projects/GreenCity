package greencity.entity;

import greencity.enums.HabitAssignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;

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
     * This variable shows that the user has confirmed the seen notification that
     * habit has enough progress (from 80 to 100 %) to be in status ACQUIRED. Now
     * user can change status from INPROGRESS to ACQUIRED.
     *
     */
    @Column(name = "progress_notification_has_confirmed", nullable = false)
    private Boolean progressNotificationHasConfirmed;

    @OneToMany(mappedBy = "habitAssign", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserShoppingListItem> userShoppingListItems;

    @ManyToOne(fetch = FetchType.LAZY)
    private Habit habit;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "habitAssign", cascade = CascadeType.ALL)
    private List<HabitStatistic> habitStatistic;

    @OneToMany(mappedBy = "habitAssign", cascade = CascadeType.ALL)
    private List<HabitStatusCalendar> habitStatusCalendars;
}
