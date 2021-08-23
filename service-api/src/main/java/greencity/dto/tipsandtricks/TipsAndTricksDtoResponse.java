package greencity.dto.tipsandtricks;

import greencity.dto.user.AuthorDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TipsAndTricksDtoResponse implements Serializable {
    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    private String titleTranslation;

    @NotEmpty
    private String textTranslation;

    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;

    @NotEmpty
    private transient AuthorDto author;

    @NotEmpty
    private List<String> tags;

    private String imagePath;

    private String source;
}
