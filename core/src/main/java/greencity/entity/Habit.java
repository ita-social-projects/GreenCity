package greencity.entity;

import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "habits")
@EqualsAndHashCode(
    exclude = {"habitAssigns", "habitTranslations", "habitStatistics"})
@ToString(
    exclude = {"habitAssigns", "habitTranslations", "habitStatistics"})
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "image", nullable = false)
    private String image;

    @OneToMany(mappedBy = "habit", fetch = FetchType.LAZY)
    private List<HabitTranslation> habitTranslations;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    private List<HabitAssign> habitAssigns;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    private List<HabitStatistic> habitStatistics;
}
