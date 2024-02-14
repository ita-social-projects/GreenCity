package greencity.dto.advice;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.Valid;
import java.util.List;

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
