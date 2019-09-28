package greencity.dto.discount;

import greencity.dto.specification.SpecificationNameDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountValueDto {
    private int value;
    private SpecificationNameDto specification;
}
