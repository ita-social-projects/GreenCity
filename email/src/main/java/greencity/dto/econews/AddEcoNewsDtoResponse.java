package greencity.dto.econews;

import greencity.dto.tag.TagDto;
import greencity.dto.user.EcoNewsAuthorDto;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEcoNewsDtoResponse implements Serializable {
    @Min(1)
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String text;
    @NotEmpty
    private EcoNewsAuthorDto ecoNewsAuthorDto;
    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;
    @NotEmpty
    private String imagePath;
    @NotEmpty
    private List<TagDto> tags;
}
