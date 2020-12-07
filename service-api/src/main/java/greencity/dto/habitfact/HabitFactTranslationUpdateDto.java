package greencity.dto.habitfact;

import greencity.dto.language.LanguageDTO;
import greencity.enums.FactOfDayStatus;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitFactTranslationUpdateDto {
    @NotNull
    private FactOfDayStatus factOfDayStatus;

    @NotNull
    private LanguageDTO language;

    @NotBlank
    private String content;
}
