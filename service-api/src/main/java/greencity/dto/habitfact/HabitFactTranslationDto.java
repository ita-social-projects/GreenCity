package greencity.dto.habitfact;

import greencity.dto.language.LanguageDTO;
import greencity.enums.FactOfDayStatus;
import lombok.*;

@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitFactTranslationDto {
    private Long id;

    private FactOfDayStatus factOfDayStatus;

    private LanguageDTO language;

    private String content;
}
