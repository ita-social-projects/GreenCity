package greencity.dto.achievementcategory;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.useraction.UserActionVO;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class AchievementCategoryVO {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;

    private List<AchievementVO> achievementList;

    private List<UserActionVO> userActions;
}
