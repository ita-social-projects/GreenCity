package greencity.dto.achievement;

import greencity.dto.language.LanguageVO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AchievementTranslationVO {
    private Long id;

    private LanguageVO language;

    private String title;

    private String description;

    private String message;
}
