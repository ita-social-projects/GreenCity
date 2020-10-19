package greencity.dto.translation;

import greencity.dto.language.LanguageVO;
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

    private LanguageVO language;

    private String content;
}
