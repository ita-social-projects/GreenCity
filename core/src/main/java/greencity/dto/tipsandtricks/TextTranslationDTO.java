package greencity.dto.tipsandtricks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextTranslationDTO {
    @NotEmpty
    @Size(min = 20, max = 4000)
    private String content;
    @NotNull
    private String languageCode;
}
