package greencity.dto.goal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class GoalRequestDto {
    @NotNull
    @Min(value = 1, message = "Goal id must be a positive number")
    private Long id = 0L;
}
