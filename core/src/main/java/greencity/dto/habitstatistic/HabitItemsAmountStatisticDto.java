package greencity.dto.habitstatistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitItemsAmountStatisticDto {
    private String habitItem;
    private long notTakenItems;
}
