package greencity.dto.habitstatistic;

import greencity.entity.enums.HabitRate;
import java.time.LocalDate;
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
    private LocalDate createdOn;
    private Integer amountOfItems;
}
