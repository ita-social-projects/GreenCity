package greencity.dto.econews;

import greencity.dto.user.EcoNewsAuthorDto;
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
public class EcoNewsDto {
    @NotEmpty
    private ZonedDateTime creationDate;

    @NotEmpty
    private String imagePath;

    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String text;

    private String source;

    @NotEmpty
    private EcoNewsAuthorDto author;

    @NotEmpty
    private List<String> tags;
}
