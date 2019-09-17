package greencity.dto.filter;

import greencity.dto.location.MapBoundsDto;
import greencity.entity.enums.PlaceStatus;
import javax.validation.Valid;
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
    private PlaceStatus status;
}
