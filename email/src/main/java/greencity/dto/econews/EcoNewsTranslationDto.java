package greencity.dto.econews;

import greencity.dto.language.LanguageRequestDto;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EcoNewsTranslationDto {
    @NotNull
    private LanguageRequestDto language;

    @NotEmpty
    @Size(max = 255)
    private String title;
}
