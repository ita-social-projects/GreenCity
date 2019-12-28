package greencity.dto.advice;

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
    private List<AdviceTranslationDTO> translations;

    @NotNull
    private HabitDictionaryIdDto habitDictionary;
}
