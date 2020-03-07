package greencity.dto.econews;

import greencity.dto.tag.TagDto;
import greencity.dto.user.EcoNewsAuthorDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @NotEmpty
    private EcoNewsAuthorDto author;
    @NotEmpty
    private List<TagDto> tags;

}
