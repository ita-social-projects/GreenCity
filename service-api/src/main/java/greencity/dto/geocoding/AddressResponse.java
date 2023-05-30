package greencity.dto.geocoding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Setter
public class AddressResponse {
    private String street;
    private String houseNumber;
    private String city;
    private String region;
    private String country;
    private String formattedAddress;
}