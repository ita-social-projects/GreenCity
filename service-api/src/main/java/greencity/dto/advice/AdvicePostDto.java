package greencity.dto.advice;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.user.HabitIdRequestDto;
import greencity.dto.language.LanguageTranslationDTO;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AdvicePostDto {
    @Valid
    @LanguageTranslationConstraint
    private List<LanguageTranslationDTO> translations;

    @Valid
    private HabitIdRequestDto habit;
}
