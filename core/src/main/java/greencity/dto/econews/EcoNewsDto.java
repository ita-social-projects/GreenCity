package greencity.dto.econews;

import greencity.dto.user.EcoNewsAuthorDto;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode

public class EcoNewsDto {
    @NotEmpty
    private ZonedDateTime creationDate;
    @NotEmpty
    private String imagePath;
    @NotNull
    @Min(0)
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
