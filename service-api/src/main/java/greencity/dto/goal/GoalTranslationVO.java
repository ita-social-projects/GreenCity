package greencity.dto.goal;

import greencity.dto.translation.TranslationVO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true, exclude = "goal")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class GoalTranslationVO extends TranslationVO {
    private GoalVO goal;
}
