package greencity.dto.habitstatus;

import greencity.dto.habit.HabitAssignVO;
import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitStatusVO {
    private Long id;
    private Integer workingDays;
    private Integer habitStreak;
    private LocalDateTime lastEnrollmentDate;
    private HabitAssignVO habitAssignVO;
}
