package greencity.dto.breaktime;

import java.time.LocalTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BreakTimeVO {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
}
