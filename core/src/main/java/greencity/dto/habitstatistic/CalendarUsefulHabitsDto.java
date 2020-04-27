package greencity.dto.habitstatistic;

import greencity.dto.user.HabitLogItemDto;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class CalendarUsefulHabitsDto {
    private ZonedDateTime creationDate;
    private List<HabitLogItemDto> allItemsPerMonth;
    private List<HabitLogItemDto> differenceUnTakenItemsWithPreviousDay;
}
