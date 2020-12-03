package greencity.dto.habit;

import greencity.enums.HabitAssignStatus;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import java.time.ZonedDateTime;
import java.util.List;
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
    private List<HabitStatusCalendarDto> habitStatusCalendarDtoList;
}
