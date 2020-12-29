package greencity.dto.habitstatuscalendar;

import greencity.dto.habit.HabitAssignVO;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class HabitStatusCalendarVO {
    private Long id;
    private LocalDate enrollDate;
    private HabitAssignVO habitAssignVO;
}
