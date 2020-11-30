package greencity.dto.factoftheday;

import greencity.dto.language.LanguageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactOfTheDayTranslationEmbeddedDTO {
    private Long id;

    private String content;

    private LanguageDTO language;
}
