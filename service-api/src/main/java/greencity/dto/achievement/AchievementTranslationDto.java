package greencity.dto.achievement;

import greencity.dto.language.LanguageVO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AchievementTranslationDto {
    private Long id;
    private String name;
    private String nameEng;
    private AchievementVO achievementVO;
}
