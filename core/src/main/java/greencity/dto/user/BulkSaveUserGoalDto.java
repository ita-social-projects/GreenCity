package greencity.dto.user;

import java.util.List;
import javax.validation.Valid;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BulkSaveUserGoalDto {
    @Valid List<@Valid UserGoalDto> userGoals;
    @Valid List<@Valid UserCustomGoalDto> userCustomGoal;
}
