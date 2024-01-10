package greencity.dto.habitfact;

import greencity.dto.language.LanguageDTO;
import greencity.enums.FactOfDayStatus;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
