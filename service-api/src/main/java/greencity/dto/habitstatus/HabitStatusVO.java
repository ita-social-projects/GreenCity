package greencity.dto.habitstatus;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HabitStatusVO {
    private Long id;
    private Integer workingDays;
    private Integer habitStreak;
    private LocalDateTime lastEnrollmentDate;
    private HabitAssignVO habitAssignVO;
    private List<HabitStatusCalendarVO> habitStatusCalendarVOS;
}
