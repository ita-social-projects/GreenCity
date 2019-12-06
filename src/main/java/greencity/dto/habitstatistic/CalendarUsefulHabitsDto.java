package greencity.dto.habitstatistic;

import greencity.dto.user.HabitLogItemDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarUsefulHabitsDto {
    private LocalDate creationDate;
    private List<HabitLogItemDto> allItemsPerMonth;
    private List<HabitLogItemDto> differenceUnTakenItemsWithPreviousDay;
}
