package greencity.dto.habitstatistic;

import java.time.LocalDate;
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
    private Map<String, Integer> amountUnTakenItemsPerMonth;
    private Map<String, Integer> differenceUnTakenItemsWithPreviousMonth;
}
