package greencity.dto.goal;

import greencity.dto.advice.AdviceVO;
import greencity.dto.language.LanguageVO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class GoalTranslationVO {
    private Long id;

    private LanguageVO language;

    private GoalVO goal;

    private String content;
}
