package greencity.entity.localization;

import greencity.entity.Goal;
import greencity.entity.Translation;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "goal_translations")
@EqualsAndHashCode(callSuper = true, exclude = "goal")
@SuperBuilder
@NoArgsConstructor
public class GoalTranslation extends Translation {
    @Getter
    @Setter
    @ManyToOne
    private Goal goal;
}
