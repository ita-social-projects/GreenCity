package greencity.dto.user;

import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkUserGoalDto {
    @Valid List<@Valid UserGoalRequestDto> userGoal;
}
