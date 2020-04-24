package greencity.entity;

import greencity.entity.localization.GoalTranslation;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "goal", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
    private List<GoalTranslation> translations;
}
