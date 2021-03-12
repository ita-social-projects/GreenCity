package greencity.dto.factoftheday;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactOfTheDayTranslationDTO {
    private Long id;

    private String content;
}
