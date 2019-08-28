package greencity.dto.place;

import greencity.dto.location.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceByBoundsDto {
    private Long id;
    private String name;
    private LocationDto location;
}
