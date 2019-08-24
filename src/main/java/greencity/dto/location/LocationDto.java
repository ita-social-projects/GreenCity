package greencity.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    private Long id;
    private Double lat;
    private Double lng;
    private String address;
}
