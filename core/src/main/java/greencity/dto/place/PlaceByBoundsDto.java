package greencity.dto.place;

import greencity.dto.location.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceByBoundsDto {
    private Long id;
    private String name;
    private LocationDto location;
}
