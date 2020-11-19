package greencity.dto.achievementcategory;

import greencity.dto.achievement.AchievementVO;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AchievementCategoryVO {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;
}
