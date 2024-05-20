package greencity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Data
public class AddressDto {
    private Double latitude;
    private Double longitude;
    private String streetEn;
    private String streetUa;
    private String houseNumber;
    private String cityEn;
    private String cityUa;
    private String regionEn;
    private String regionUa;
    private String countryEn;
    private String countryUa;
    private String formattedAddressEn;
    private String formattedAddressUa;
}
