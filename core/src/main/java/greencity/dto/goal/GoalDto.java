package greencity.dto.goal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class GoalDto {
    @NotNull
    @Min(value = 1, message = "Goal id must be a positive number")
    private Long id;

    @NotEmpty(message = "Goal text must not be null")
    private String text;
}
