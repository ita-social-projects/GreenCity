package greencity.dto.language;

import greencity.constant.ServiceValidationConstants;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageTranslationVO {
    @NotNull
    private LanguageDTO language;

    @NotBlank
    @Size(min = ServiceValidationConstants.ADVICE_MIN_LENGTH, max = ServiceValidationConstants.ADVICE_MAX_LENGTH)
    private String content;
}
