package greencity.dto.tipsandtricks;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
public class TipsAndTricksDtoManagement implements Serializable {
    @NotNull
    @Min(value = 1, message = "Tips and Tricks id must be a positive number")
    private Long id;

    @Size(min = 1, max = 170, message = "size must be between 1 and 170")
    private String title;

    @Size(min = 20, max = 63206, message = "size must be between 20 and 63206")
    private String text;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;

    @Email
    private String emailAuthor;

    @NotEmpty(message = "at least one tag")
    private List<String> tags;

    private String imagePath;

    private String source;
}
