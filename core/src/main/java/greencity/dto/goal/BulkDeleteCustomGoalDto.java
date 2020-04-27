package greencity.dto.goal;

import java.util.List;
import javax.validation.Valid;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BulkDeleteCustomGoalDto {
    @Valid List<@Valid CustomGoalRequestDto> customGoals;
}
