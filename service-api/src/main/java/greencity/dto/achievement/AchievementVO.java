package greencity.dto.achievement;

import greencity.dto.achievementcategory.AchievementCategoryVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementVO {

    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotEmpty
    private String message;

    private List<UserAchievementVO> userAchievements;

    @NotEmpty
    private AchievementCategoryVO achievementCategory;

    @NotEmpty
    private Integer condition;
}
