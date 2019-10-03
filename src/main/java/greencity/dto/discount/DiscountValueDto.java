package greencity.dto.discount;

import greencity.constant.ValidationConstants;
import greencity.dto.specification.SpecificationNameDto;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountValueDto {
    @Range(min = 0, message = ValidationConstants.DISCOUNT_VALUE_MIN_ERROR)
    @Range(max = 100, message = ValidationConstants.DISCOUNT_VALUE_MAX_ERROR)
    private int value = 0;

    @Valid
    private SpecificationNameDto specification;
}
