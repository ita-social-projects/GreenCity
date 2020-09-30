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
public class HabitStatusDto {
    @NotNull
    @Min(1)
    private Long id;
    @NotEmpty
    private Integer workingDays;
    @NotEmpty
    private Integer habitStreak;
    @NotEmpty
    private boolean suspended;
    @NotEmpty
    private LocalDateTime lastEnrollmentDate;
    @NotEmpty
    private HabitAssignDto habitAssign;
}
