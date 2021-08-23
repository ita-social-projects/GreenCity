package greencity.dto.tipsandtricks;

import greencity.constant.ServiceValidationConstants;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class TipsAndTricksDtoRequest {
    @Valid
    private TitleTranslationEmbeddedPostDTO titleTranslation;

    @Valid
    private TextTranslationDTO textTranslation;

    @NotEmpty(message = ServiceValidationConstants.MIN_AMOUNT_OF_TAGS)
    private List<String> tags;

    private String image;

    private String source;
}
