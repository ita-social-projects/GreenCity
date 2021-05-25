package greencity.dto.location;

import greencity.dto.place.PlaceVO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LocationVO {
    private Long id;
    private Double lat;
    private Double lng;
    private String address;
    private PlaceVO place;
}
