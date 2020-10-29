package greencity.dto.language;

import greencity.constant.ServiceValidationConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageTranslationDTO {
    @NotNull
    private LanguageDTO language;

    @NotBlank
    @Size(min = ServiceValidationConstants.ADVICE_MIN_LENGTH, max = ServiceValidationConstants.ADVICE_MAX_LENGTH)
    private String content;
}
