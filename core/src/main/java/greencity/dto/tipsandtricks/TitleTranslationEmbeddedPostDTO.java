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
public class TitleTranslationEmbeddedPostDTO {
    @NotEmpty
    @Size(min = 1, max = 170)
    private String content;
    @NotNull
    private String languageCode;
}
