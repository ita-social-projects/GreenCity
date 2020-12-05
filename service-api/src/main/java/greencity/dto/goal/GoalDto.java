package greencity.dto.goal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class GoalDto {
    @NotNull
    @Min(value = 1)
    private Long id;

    @NotEmpty
    private String text;
}
