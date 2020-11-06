package greencity.dto.tipsandtricks;

import greencity.constant.ServiceValidationConstants;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TipsAndTricksDtoManagement {
    private Long id;

    @Valid
    private List<TitleTranslationEmbeddedPostDTO> titleTranslations;

    @Valid
    private List<TextTranslationDTO> textTranslations;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;

    private String authorName;

    @NotEmpty(message = ServiceValidationConstants.MIN_AMOUNT_OF_TAGS)
    private List<String> tags;

    private String imagePath;

    private String source;
}
