package greencity.dto.advice;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class AdviceVO {
    private Long id;

    @LanguageTranslationConstraint
    private List<LanguageTranslationDTO> translations;

    @NotNull
    private HabitIdRequestDto habit;
}
