package greencity.dto.discount;

import greencity.constant.ValidationConstants;
import greencity.dto.specification.SpecificationNameDto;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountDtoForUpdatePlace {
    @Range(min = ValidationConstants.DISCOUNT_VALUE_MIN,
        max = ValidationConstants.DISCOUNT_VALUE_MAX)
    private int value;

    @Valid
    private SpecificationNameDto specification;
}
