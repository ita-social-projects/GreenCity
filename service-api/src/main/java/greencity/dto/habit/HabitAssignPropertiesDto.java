package greencity.dto.habit;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignPropertiesDto {
    @NotNull
    @Min(1)
    private Integer duration;
}
