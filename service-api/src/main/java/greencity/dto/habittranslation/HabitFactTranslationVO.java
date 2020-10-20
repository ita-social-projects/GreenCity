package greencity.dto.habittranslation;

import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.translation.TranslationVO;
import greencity.enums.FactOfDayStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class HabitFactTranslationVO extends TranslationVO {
    private FactOfDayStatus factOfDayStatus;

    private HabitFactVO habitFact;
}
