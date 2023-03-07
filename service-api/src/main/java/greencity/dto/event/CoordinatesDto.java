package greencity.dto.event;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@Setter
public class CoordinatesDto {
    private double latitude;
    private double longitude;
    private String streetEn;
    private String streetUa;
    private String houseNumber;
    private String cityEn;
    private String cityUa;
    private String regionEn;
    private String regionUa;
    private String countryEn;
    private String countryUa;
}
