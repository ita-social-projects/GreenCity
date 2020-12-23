package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AchievementNotification {
    private Long id;

    private List<AchievementTranslationVO> translations;

    private AchievementCategoryVO achievementCategory;
}
