package greencity.dto.tipsandtricks;

import greencity.dto.translation.TranslationVO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class TextTranslationVO extends TranslationVO {
    private TipsAndTricksVO tipsAndTricks;
}
