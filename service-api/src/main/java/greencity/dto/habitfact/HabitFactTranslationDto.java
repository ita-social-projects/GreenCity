package greencity.dto.habitfact;

import greencity.dto.language.LanguageDTO;
import greencity.enums.FactOfDayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitFactTranslationDto {
    private Long id;

    private FactOfDayStatus factOfDayStatus;

    private LanguageDTO language;

    private String content;
}
