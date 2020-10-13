package greencity.dto.advice;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.user.HabitIdRequestDto;
import greencity.dto.language.LanguageTranslationVO;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AdvicePostDto {
    @LanguageTranslationConstraint
    private List<LanguageTranslationVO> translations;

    @NotNull
    private HabitIdRequestDto habit;
}
