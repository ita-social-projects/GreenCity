package greencity.dto.tipsandtricks;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static greencity.constant.ValidationConstants.MIN_AMOUNT_OF_TAGS;

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

    @NotEmpty(message = MIN_AMOUNT_OF_TAGS)
    private List<String> tags;

    private String image;

    private String source;
}
