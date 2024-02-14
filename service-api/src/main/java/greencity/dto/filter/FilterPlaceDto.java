package greencity.dto.filter;

import greencity.dto.location.MapBoundsDto;
import greencity.enums.PlaceStatus;
import jakarta.validation.Valid;
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
    private String time;
    @Valid
    private FilterDistanceDto distanceFromUserDto;
    private String searchReg;
    private String[] categories;
}
