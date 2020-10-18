package greencity.dto.habitstatus;

import java.time.LocalDateTime;
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
