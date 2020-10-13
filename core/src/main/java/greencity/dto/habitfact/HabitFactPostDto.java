package greencity.dto.habitfact;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.user.HabitIdRequestDto;
import greencity.dto.language.LanguageTranslationVO;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitFactPostDto {
    @LanguageTranslationConstraint
    private List<LanguageTranslationVO> translations;

    @NotNull
    private HabitIdRequestDto habit;
}
