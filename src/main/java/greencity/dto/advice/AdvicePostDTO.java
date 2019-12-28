package greencity.dto.advice;

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
public class AdvicePostDTO {
    @NotNull
    private List<LanguageTranslationDTO> translations;

    @NotNull
    private HabitDictionaryIdDto habitDictionary;
}
