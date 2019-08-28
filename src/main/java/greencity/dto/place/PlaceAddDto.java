package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.dto.category.CategoryDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.entity.enums.PlaceStatus;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceAddDto {

    @NotBlank(message = ValidationConstants.EMPTY_PLACE_NAME)
    @Size(max = ValidationConstants.PLACE_NAME_MAX_LENGTH)
    private String name;

    @NotNull
    private LocationAddressAndGeoDto location;

    @NotNull
    private CategoryDto category;

    @NotNull
    private List<OpeningHoursDto> openingHoursList;

    @NotNull private PlaceStatus placeStatus = PlaceStatus.PROPOSED;
}
