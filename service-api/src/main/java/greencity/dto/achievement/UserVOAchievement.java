package greencity.dto.achievement;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class UserVOAchievement {
    private Long id;

    private String name;

    private List<UserAchievementVO> userAchievements;
}
