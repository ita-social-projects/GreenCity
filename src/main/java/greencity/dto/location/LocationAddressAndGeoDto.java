package greencity.dto.location;

import greencity.constant.ValidationConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationAddressAndGeoDto {

    @NotBlank(message = ValidationConstants.EMPTY_PLACE_ADDRESS)
    @Size(max = ValidationConstants.PLACE_ADDRESS_MAX_LENGTH)
    private String address;

    @NotNull private Double lat;

    @NotNull private Double lng;
}
