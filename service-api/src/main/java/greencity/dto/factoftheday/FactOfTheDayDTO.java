package greencity.dto.factoftheday;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactOfTheDayDTO {
    private Long id;
    private String name;
    private List<FactOfTheDayTranslationEmbeddedDTO> factOfTheDayTranslations;
    private ZonedDateTime createDate;
}
