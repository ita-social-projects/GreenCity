package greencity.dto.tipsandtricks;

import greencity.dto.user.TipsAndTricksAuthorDto;
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
public class AddTipsAndTricksDtoResponse implements Serializable {
    @NotNull
    @Min(value = 1, message = "Tips and Tricks id must be a positive number")
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String text;

    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;

    @NotEmpty
    private TipsAndTricksAuthorDto author;

    @NotEmpty
    private List<String> tipsAndTricksTags;

    private String imagePath;

    private String source;
}
