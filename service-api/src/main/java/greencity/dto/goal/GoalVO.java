package greencity.dto.goal;

import greencity.dto.user.UserGoalDto;
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

    private List<UserGoalDto> userGoals;

    private List<GoalTranslationDTO> translations;
}
