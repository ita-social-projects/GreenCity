package greencity.dto.econews;

import greencity.dto.language.LanguageRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EcoNewsTranslationDto {
    @NotNull
    private LanguageRequestDto language;

    @NotEmpty
    @Size(max = 255)
    private String title;

    @NotEmpty
    @Size(max = 255)
    private String text;
}
