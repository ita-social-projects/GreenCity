package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.enums.AchievementStatus;
import greencity.enums.UserActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementVO {
    private Long id;

    private List<AchievementTranslationVO> translations;

    private List<UserAchievementVO> userAchievements;

    @NotEmpty
    private AchievementCategoryVO achievementCategory;

    private AchievementStatus achievementStatus;

    @NotEmpty
    private Map<UserActionType, Long> condition;

    private String icon;
}
