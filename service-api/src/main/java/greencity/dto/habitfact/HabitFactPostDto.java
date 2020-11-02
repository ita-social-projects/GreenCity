package greencity.dto.habitfact;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.user.HabitIdRequestDto;
import greencity.dto.language.LanguageTranslationDTO;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitFactPostDto {
    @LanguageTranslationConstraint
    private List<LanguageTranslationDTO> translations;

    @NotNull
    private HabitIdRequestDto habit;
}
