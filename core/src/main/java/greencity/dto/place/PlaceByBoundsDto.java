package greencity.dto.place;

import greencity.dto.location.LocationDto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class PlaceByBoundsDto {
    private Long id;
    private String name;
    private LocationDto location;
}
