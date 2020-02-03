package greencity.dto.habitstatistic;

import greencity.dto.user.HabitLogItemDto;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarUsefulHabitsDto {
    private ZonedDateTime creationDate;
    private List<HabitLogItemDto> allItemsPerMonth;
    private List<HabitLogItemDto> differenceUnTakenItemsWithPreviousDay;
}
