package greencity.dto.tipsandtricks;

import greencity.validator.ValidationConstants;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TipsAndTricksDtoRequest {
    @Valid
    private TitleTranslationEmbeddedPostDTO titleTranslation;

    @Valid
    private TextTranslationDTO textTranslation;
    @NotEmpty(message = ValidationConstants.MIN_AMOUNT_OF_TAGS)
    private List<String> tags;

    private String image;

    private String source;
}
