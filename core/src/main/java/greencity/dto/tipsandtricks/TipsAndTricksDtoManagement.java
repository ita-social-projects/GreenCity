package greencity.dto.tipsandtricks;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import static greencity.constant.ValidationConstants.MIN_AMOUNT_OF_TAGS;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TipsAndTricksDtoManagement implements Serializable {
    private Long id;

    @Valid
    private List<TitleTranslationEmbeddedPostDTO> titleTranslations;

    @Valid
    private List<TextTranslationDTO> textTranslations;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;

    private String authorName;

    @NotEmpty(message = MIN_AMOUNT_OF_TAGS)
    private List<String> tags;

    private String imagePath;

    private String source;
}
