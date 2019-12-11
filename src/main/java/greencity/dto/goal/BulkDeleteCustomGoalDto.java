package greencity.dto.goal;

import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkDeleteCustomGoalDto {
    @Valid List<@Valid CustomGoalRequestDto> customGoals;
}
