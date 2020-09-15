package greencity.dto.habitstatistic;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.HabitRate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UpdateHabitStatisticDto {
    @Min(0)
    private Long id;
    @Range(min = ValidationConstants.MIN_AMOUNT_OF_ITEMS, max = ValidationConstants.MAX_AMOUNT_OF_ITEMS)
    @NotNull
    private Integer amountOfItems;
    @NotNull
    private HabitRate habitRate;
}
