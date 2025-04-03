package greencity.dto.econews;

import java.time.ZonedDateTime;
import java.util.List;

import greencity.dto.user.EcoNewsAuthorDto;
import jakarta.validation.constraints.NotEmpty;
import greencity.dto.tag.TagUkEnNamesDto;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper=true)
public class EcoNewsGroupedTagsDto extends BaseEcoNewsDto{
    @NotEmpty
    private List<TagUkEnNamesDto> tags;

    public EcoNewsGroupedTagsDto(List<TagUkEnNamesDto> tags, ZonedDateTime creationDate, String imagePath, Long id,
                                 String title, String content, String shortInfo, EcoNewsAuthorDto author,
                                 int likes, int dislikes, int countComments, boolean hidden) {
        super(creationDate, imagePath, id, title, content, shortInfo, author, likes, dislikes, countComments, hidden);
        this.tags = tags;
    }
}
