package greencity.dto.goal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CustomGoalResponseDto {
    @NonNull
    @Min(value = 1, message = "Goal id must be a positive number")
    private Long id;
    @NotEmpty
    private String text;
}
