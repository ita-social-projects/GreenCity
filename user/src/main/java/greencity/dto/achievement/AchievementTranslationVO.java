package greencity.dto.achievement;

import greencity.dto.language.LanguageVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
