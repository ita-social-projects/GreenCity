package greencity.dto.goal;

import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomGoalResponseDto {
    @NonNull
    @Min(value = 1, message = "Goal id must be a positive number")
    private Long id;
    @NonNull
    private String text;
}
