package greencity.dto.econews;

import java.util.List;
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
@EqualsAndHashCode(callSuper = true)
public class EcoNewsGroupedTagsDto extends BaseEcoNewsDto {
    @NotEmpty
    private List<TagUkEnNamesDto> tags;
}
