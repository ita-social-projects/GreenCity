package greencity.dto.place;

import greencity.entity.Location;
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
    private Location location;
}
