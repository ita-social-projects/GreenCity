package greencity.dto.factoftheday;

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
public class FactOfTheDayDTO {
    private Long id;
    private String name;
    private List<FactOfTheDayTranslationEmbeddedDTO> factOfTheDayTranslations;
    private ZonedDateTime createDate;
    private Set<TagDto> tags;
}
