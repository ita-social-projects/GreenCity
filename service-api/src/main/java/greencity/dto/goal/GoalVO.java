package greencity.dto.goal;

import greencity.dto.user.UserGoalVO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class GoalVO {
    private Long id;

    private List<UserGoalVO> userGoals;

    private List<GoalTranslationVO> translations;
}
