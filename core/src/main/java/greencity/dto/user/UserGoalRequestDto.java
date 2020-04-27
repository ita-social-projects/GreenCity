package greencity.dto.user;

import javax.validation.constraints.Min;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserGoalRequestDto {
    @NonNull
    @Min(value = 1, message = "Goal id must be a positive number")
    private Long id = 0L;
}
