package greencity.dto.achievementcategory;

import greencity.dto.achievement.AchievementVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AchievementCategoryVO {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private AchievementVO category;
}
