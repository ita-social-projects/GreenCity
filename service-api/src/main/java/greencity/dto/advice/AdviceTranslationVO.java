package greencity.dto.advice;

import greencity.dto.language.LanguageVO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AdviceTranslationVO {
    private Long id;

    private LanguageVO language;

    private String content;
}
