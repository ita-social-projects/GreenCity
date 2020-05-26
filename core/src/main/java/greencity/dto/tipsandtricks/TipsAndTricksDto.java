package greencity.dto.tipsandtricks;

import greencity.dto.user.TipsAndTricksAuthorDto;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "author")
@EqualsAndHashCode
public class TipsAndTricksDto {
    @NotNull
    @Min(value = 1, message = "Tips and Tricks id must be a positive number")
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String text;

    @NotEmpty
    private TipsAndTricksAuthorDto author;

    @NotEmpty
    private ZonedDateTime creationDate;

    @NotEmpty
    private List<String> tipsAndTricksTags;

    private String imagePath;

    private String source;
}
