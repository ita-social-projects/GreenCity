package greencity.dto.habitstatistic;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
