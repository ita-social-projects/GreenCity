package greencity.dto.language;

import greencity.constant.ValidationConstants;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageTranslationDTO {
    @NotNull
    private LanguageDTO language;

    @NotBlank
    @Size(min = ValidationConstants.ADVICE_MIN_LENGTH, max = ValidationConstants.ADVICE_MAX_LENGTH)
    private String content;
}
