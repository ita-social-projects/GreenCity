package greencity.dto.goal;

import greencity.dto.language.LanguageVO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalTranslationDTO {
    private Long id;

    private LanguageVO language;

    private String content;
}
