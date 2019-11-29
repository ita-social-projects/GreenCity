package greencity.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Byte statusHabit;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @OneToMany(mappedBy = "habit", cascade = {CascadeType.ALL})
    private List<HabitStatistic> habitStatistics;
}
