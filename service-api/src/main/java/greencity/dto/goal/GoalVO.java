package greencity.dto.goal;

import greencity.dto.user.UserGoalDto;
import greencity.dto.user.UserGoalVO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class GoalVO {
    private Long id;

    private List<UserGoalVO> userGoals;

    private List<GoalTranslationVO> translations;
}
