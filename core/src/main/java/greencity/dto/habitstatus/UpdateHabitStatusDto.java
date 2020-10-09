package greencity.dto.habitstatus;

import greencity.dto.habit.HabitAssignDto;
import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UpdateHabitStatusDto {
    @NotNull
    private Integer workingDays;
    @NotNull
    private Integer habitStreak;
    @NotNull
    private LocalDateTime lastEnrollmentDate;
}
