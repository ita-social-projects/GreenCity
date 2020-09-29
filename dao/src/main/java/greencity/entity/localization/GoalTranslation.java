package greencity.entity.localization;

import greencity.entity.Goal;
import greencity.entity.Language;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "goal_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "goal")
@ToString(exclude = "goal")
@Builder
public class GoalTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Language language;

    @Column(nullable = false)
    private String text;

    @ManyToOne
    private Goal goal;
}
