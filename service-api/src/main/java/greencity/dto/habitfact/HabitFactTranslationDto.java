package greencity.dto.habitfact;

import greencity.dto.language.LanguageDTO;
import greencity.enums.FactOfDayStatus;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

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
