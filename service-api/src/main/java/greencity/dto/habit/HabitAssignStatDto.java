package greencity.dto.habit;

import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignStatDto {
    @NotNull
    private Boolean acquired;
    @NotNull
    private Boolean suspended;
}
