package greencity.dto.discount;

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
    @Range(min = 0, message = "min value is 0.")
    @Range(max = 100, message = "max value is 100.")
    private int value = 0;

    @Valid
    private SpecificationNameDto specification;
}
