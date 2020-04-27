package greencity.dto.habitstatistic;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitItemsAmountStatisticDto {
    private String habitItem;
    private long notTakenItems;
}
