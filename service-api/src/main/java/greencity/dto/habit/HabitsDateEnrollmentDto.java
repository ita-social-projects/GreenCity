package greencity.dto.habit;

import java.time.LocalDate;
import java.util.List;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class HabitsDateEnrollmentDto {
    private LocalDate enrollDate;
    private List<HabitEnrollDto> habitAssigns;
}
