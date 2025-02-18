package greencity.dto.factoftheday;

import greencity.annotations.SortableField;
import greencity.dto.SortableDTO;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import greencity.dto.tag.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactOfTheDayDTO implements SortableDTO {
    @SortableField
    private Long id;
    @SortableField
    private String name;
    private List<FactOfTheDayTranslationEmbeddedDTO> factOfTheDayTranslations;
    @SortableField
    private ZonedDateTime createDate;
    private Set<TagDto> tags;
}
