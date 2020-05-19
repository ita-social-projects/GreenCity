package greencity.dto.user;

import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserGoalRequestDto {
    @NonNull
    @Min(value = 1, message = "Goal id must be a positive number")
    private Long id;
}
