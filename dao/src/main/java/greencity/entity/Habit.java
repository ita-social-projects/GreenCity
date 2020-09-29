package greencity.entity;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "habits")
@EqualsAndHashCode(
    exclude = {"users", "habitDictionary", "habitStatistics"})
@ToString(
    exclude = {"users", "habitDictionary", "habitStatistics"})
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    private HabitDictionary habitDictionary;

    @ManyToMany
    @JoinTable(
        name = "habits_users_assign",
        joinColumns = @JoinColumn(name = "habit_id"),
        inverseJoinColumns = @JoinColumn(name = "users_id"))
    private List<User> users;

    @Column(name = "status", nullable = false)
    private Boolean statusHabit;

    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;

    @OneToMany(mappedBy = "habit", cascade = {CascadeType.ALL})
    private List<HabitStatistic> habitStatistics;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    private List<HabitStatus> habitStatuses;
}
