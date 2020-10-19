package greencity.dto.factoftheday;

import greencity.dto.translation.TranslationVO;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "factOfTheDay")
@NoArgsConstructor
public class FactOfTheDayTranslationVO extends TranslationVO {
    private FactOfTheDayVO factOfTheDay;
}
