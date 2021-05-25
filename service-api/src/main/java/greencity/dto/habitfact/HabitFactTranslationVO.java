package greencity.dto.habitfact;

import greencity.dto.translation.TranslationVO;
import greencity.enums.FactOfDayStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class HabitFactTranslationVO extends TranslationVO {
    private FactOfDayStatus factOfDayStatus;

    private HabitFactVO habitFact;
}
