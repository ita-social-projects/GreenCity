package greencity.dto.filter;

import greencity.dto.category.CategoryDto;
import greencity.dto.specification.SpecificationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDiscountDto {
    private CategoryDto category;
    private SpecificationDto specification;
    private int discountMin;
    private int discountMax;
}
