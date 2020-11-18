package greencity.dto.achievement;

import greencity.dto.user.UserVO;
import greencity.enums.AchievementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievementVO {

    @NotEmpty
    private Long id;

    @NotEmpty
    private UserVO user;

    @NotEmpty
    private AchievementVO achievement;

    @NotEmpty
    private AchievementStatus achievementStatus;
}
