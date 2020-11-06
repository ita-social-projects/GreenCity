package greencity.dto.goal;

import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BulkDeleteCustomGoalDto {
    @Valid
    List<@Valid CustomGoalRequestDto> customGoals;
}
