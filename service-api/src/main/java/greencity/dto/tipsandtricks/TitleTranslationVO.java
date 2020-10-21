package greencity.dto.tipsandtricks;

import greencity.dto.translation.TranslationVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
public class TitleTranslationVO extends TranslationVO {
    private TipsAndTricksVO tipsAndTricks;
}
