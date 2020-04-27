package greencity.dto.advice;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitDictionaryIdDto;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AdvicePostDTO {
    @LanguageTranslationConstraint
    private List<LanguageTranslationDTO> translations;

    @NotNull
    private HabitDictionaryIdDto habitDictionary;
}
