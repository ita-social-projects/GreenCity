package greencity.dto.econews;

import greencity.dto.tag.TagUkEnNamesDto;
import greencity.dto.user.EcoNewsAuthorDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "author")
@Builder
@EqualsAndHashCode
public class EcoNewsGroupedTagsDto {
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
    private String content;

    private String shortInfo;

    @NotEmpty
    private EcoNewsAuthorDto author;

    @NotEmpty
    private List<TagUkEnNamesDto> tags;

    private int likes;

    private int dislikes;

    private int countComments;

    private boolean hidden;
}
