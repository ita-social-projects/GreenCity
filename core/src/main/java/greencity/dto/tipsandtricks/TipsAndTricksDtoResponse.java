package greencity.dto.tipsandtricks;

import greencity.dto.user.AuthorDto;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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
    private String title;

    @NotEmpty
    private String text;

    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;

    @NotEmpty
    private AuthorDto author;

    @NotEmpty
    private List<String> tags;

    private String imagePath;

    private String source;
}
