package greencity.dto.habitstatistic;

import greencity.entity.HabitStatistic;
import greencity.entity.enums.HabitRate;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitStatisticDto {
    private Long id;
    private HabitRate habitRate;
    private ZonedDateTime createdOn;
    private Integer amountOfItems;

    /**
     * Constructor.
     */
    public HabitStatisticDto(HabitStatistic habitStatistic) {
        this.id = habitStatistic.getId();
        this.habitRate = habitStatistic.getHabitRate();
        this.createdOn = habitStatistic.getCreatedOn();
        this.amountOfItems = habitStatistic.getAmountOfItems();
    }
}
