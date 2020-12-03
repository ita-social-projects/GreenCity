package greencity.dto.habit;

import greencity.enums.HabitAssignStatus;
import java.time.ZonedDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignDto {
    private Long id;
    private HabitAssignStatus status;
    private ZonedDateTime createDateTime;
    private HabitDto habit;
    private Long userId;
    private Integer duration;
    private Integer workingDays;
    private Integer habitStreak;
    private ZonedDateTime lastEnrollmentDate;
}
