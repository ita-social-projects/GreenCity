package greencity.dto.fact;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitDictionaryIdDto;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitFactPostDTO {
    @LanguageTranslationConstraint
    private List<LanguageTranslationDTO> translations;

    @NotNull(message = "habitDictionary can not be null")
    private HabitDictionaryIdDto habitDictionary;
}
