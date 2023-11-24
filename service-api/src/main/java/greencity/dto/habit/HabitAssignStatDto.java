package greencity.dto.habit;

import greencity.enums.HabitAssignStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignStatDto {
    @NotNull
    private HabitAssignStatus status;
}
