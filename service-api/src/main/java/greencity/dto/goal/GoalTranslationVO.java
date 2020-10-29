package greencity.dto.goal;

import greencity.dto.translation.TranslationVO;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true, exclude = "goal")
@SuperBuilder
@NoArgsConstructor
public class GoalTranslationVO extends TranslationVO {
    private GoalVO goal;
}
