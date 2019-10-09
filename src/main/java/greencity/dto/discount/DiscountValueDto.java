package greencity.dto.discount;

import greencity.constant.ValidationConstants;
import greencity.dto.specification.SpecificationNameDto;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"value"})
public class DiscountValueDto {
    @Range(min = ValidationConstants.DISCOUNT_VALUE_MIN, max = ValidationConstants.DISCOUNT_VALUE_MAX,
        message = ValidationConstants.DISCOUNT_VALUE_DOES_NOT_CORRECT)
    private int value = 0;

    @Valid
    private SpecificationNameDto specification;
}
