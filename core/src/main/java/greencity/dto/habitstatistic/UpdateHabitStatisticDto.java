package greencity.dto.habitstatistic;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.HabitRate;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UpdateHabitStatisticDto {
    @Range(min = ValidationConstants.MIN_AMOUNT_OF_ITEMS, max = ValidationConstants.MAX_AMOUNT_OF_ITEMS)
    @NotNull
    private Integer amountOfItems;
    @NotNull
    private HabitRate habitRate;
}
