package greencity.dto.translation;

import greencity.dto.language.LanguageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
public class TranslationVO {
    private Long id;

    private LanguageDTO language;

    private String content;
}
