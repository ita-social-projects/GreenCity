package greencity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public class AddressDto extends UpdateAddressDto {
    private String streetEn;
    private String streetUk;
    private String houseNumber;
    private String cityEn;
    private String cityUk;
    private String regionEn;
    private String regionUk;
    private String countryEn;
    private String countryUk;
    private String formattedAddressEn;
    private String formattedAddressUk;
}
