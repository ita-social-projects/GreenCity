package greencity.entity;

import java.time.LocalDateTime;
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
    exclude = {"userId", "habitDictionaryId"})
@ToString(
    exclude = {"userId", "habitDictionaryId"})
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    private HabitDictionary habitDictionaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "status", nullable = false)
    private Boolean statusHabit;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<HabitStatistics> habitStatistics;
}
