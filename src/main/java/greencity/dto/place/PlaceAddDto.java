package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.dto.category.CategoryDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceAddDto {
    @NotBlank(message = ValidationConstants.EMPTY_PLACE_NAME)
    @Length(max = ValidationConstants.PLACE_NAME_MAX_LENGTH)
    @Pattern(regexp = "^[a-zA-Z0-9_.-]*$")
    private String name;

    @Valid
    private LocationAddressAndGeoDto location;

    @Valid
    private CategoryDto category;

    @Valid
    private List<OpeningHoursDto> openingHoursList;
}
