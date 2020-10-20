package greencity.dto.goal;

import greencity.dto.language.LanguageVO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalTranslationDTO {
    private LanguageVO language;
}
