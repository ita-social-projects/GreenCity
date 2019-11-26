package greencity.dto.habitstatistic;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.HabitRate;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHabitStatisticDto {
    @Range(min = ValidationConstants.MIN_AMOUNT_OF_ITEMS, max = ValidationConstants.MAX_AMOUNT_OF_ITEMS)
    @NotNull(message = "Amount of items can not be null.")
    private Integer amountOfItems;
    @NotNull(message = "Rate of the day can not be null")
    private HabitRate habitRate;
}
