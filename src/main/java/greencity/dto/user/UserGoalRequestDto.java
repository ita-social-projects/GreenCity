package greencity.dto.user;

import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGoalRequestDto {
    @NonNull
    @Min(value = 1, message = "Goal id must be a positive number")
    private Long id = 0L;
}
