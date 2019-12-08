package greencity.dto.goal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomGoalRequestDto {
    @NotNull
    @Min(value = 1, message = "Custom goal id must be a positive number")
    private Long id;
}
