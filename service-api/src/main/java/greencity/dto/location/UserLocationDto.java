package greencity.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLocationDto {
    private Long id;
    private String cityEn;
    private String cityUa;
    private String regionEn;
    private String regionUa;
    private String countryEn;
    private String countryUa;
    private Double latitude;
    private Double longitude;
}
