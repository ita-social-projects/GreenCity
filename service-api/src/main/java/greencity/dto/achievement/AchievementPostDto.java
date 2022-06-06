package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryDto;
import greencity.enums.UserActionType;
import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AchievementPostDto {
    private List<AchievementTranslationVO> translations;

    private AchievementCategoryDto achievementCategory;

    private Map<UserActionType, Long> condition;
}
