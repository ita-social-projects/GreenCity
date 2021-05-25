package greencity.dto.tag;

import greencity.dto.language.LanguageVO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TagTranslationVO {
    private Long id;
    private String name;
    private LanguageVO languageVO;
}
