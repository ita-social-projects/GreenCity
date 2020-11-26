package greencity.entity;

import greencity.entity.localization.GoalTranslation;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(
    exclude = {"userGoals"})
@EqualsAndHashCode(exclude = {"userGoals", "translations"})
@Table(name = "goals")
@Builder
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "goal", fetch = FetchType.LAZY)
    private List<UserGoal> userGoals;

    @OneToMany(mappedBy = "goal", fetch = FetchType.LAZY)
    private List<HabitGoal> habitGoals;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "goal", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<GoalTranslation> translations;
}
