package greencity.entity;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
    exclude = {"user", "habitDictionary", "habitStatistics"})
@ToString(
    exclude = {"user", "habitDictionary", "habitStatistics"})
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    private HabitDictionary habitDictionary;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "status", nullable = false)
    private Boolean statusHabit;

    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;

    @OneToMany(mappedBy = "habit", cascade = {CascadeType.ALL})
    private List<HabitStatistic> habitStatistics;
}
