package greencity.dto.location;

import greencity.constant.ValidationConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationAddressAndGeoDto {

    @NotBlank(message = ValidationConstants.EMPTY_PLACE_ADDRESS)
    @Size(max = ValidationConstants.PLACE_ADDRESS_MAX_LENGTH)
    private String address;

    @NotNull(message = ValidationConstants.EMPTY_VALUE_OF_LATITUDE)
    @Pattern(regexp = "[-+]?\\d+\\.\\d+")
    private Double lat;

    @NotNull(message = ValidationConstants.EMPTY_VALUE_OF_LONGITUDE)
    @Pattern(regexp = "[-+]?\\d+\\.\\d+")
    private Double lng;
}
