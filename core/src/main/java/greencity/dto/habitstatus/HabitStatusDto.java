package greencity.dto.habitstatus;

import greencity.dto.habitstatistic.HabitDto;
import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitStatusDto {
    private Long id;
    private Integer workingDays;
    private Integer habitStreak;
    private boolean suspended;
    private HabitDto habit;
    private LocalDateTime lastEnrollmentDate;
}
