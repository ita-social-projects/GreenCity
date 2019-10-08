package greencity.dto.filter;

import greencity.dto.specification.SpecificationNameDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDiscountDto {
    private SpecificationNameDto specification;
    private int discountMin;
    private int discountMax;
}
