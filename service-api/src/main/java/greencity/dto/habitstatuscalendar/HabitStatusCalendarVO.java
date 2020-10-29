package greencity.dto.habitstatuscalendar;

import greencity.dto.habitstatus.HabitStatusVO;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitStatusCalendarVO {
    private Long id;
    private LocalDate enrollDate;
    private HabitStatusVO habitStatusVO;
}
