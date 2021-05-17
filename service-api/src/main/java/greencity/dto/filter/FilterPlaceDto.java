package greencity.dto.filter;

import greencity.dto.category.CategoryDto;
import greencity.dto.category.FilterCategoryDto;
import greencity.dto.location.MapBoundsDto;
import greencity.enums.PlaceStatus;
import javax.validation.Valid;
import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterPlaceDto {
    @Valid
    private MapBoundsDto mapBoundsDto;
    private FilterDiscountDto discountDto;
    private FilterCategoryDto category;
    private PlaceStatus status;
    private String time;
    @Valid
    private FilterDistanceDto distanceFromUserDto;
    private String searchReg;
}
