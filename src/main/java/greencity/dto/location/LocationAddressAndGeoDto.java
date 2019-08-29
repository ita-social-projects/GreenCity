package greencity.dto.location;

import greencity.constant.ValidationConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationAddressAndGeoDto {

    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = ValidationConstants.LOCATION_ADDRESS_BAD_FORMED)
    @Length(
            min = ValidationConstants.PLACE_ADDRESS_MIN_LENGTH,
            max = ValidationConstants.PLACE_ADDRESS_MAX_LENGTH)
    private String address;

    @NotNull(message = ValidationConstants.EMPTY_VALUE_OF_LATITUDE)
    private Double lat;

    @NotNull(message = ValidationConstants.EMPTY_VALUE_OF_LONGITUDE)
    private Double lng;
}
