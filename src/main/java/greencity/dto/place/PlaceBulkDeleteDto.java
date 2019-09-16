package greencity.dto.place;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceBulkDeleteDto {
    private Long deletedPlaces;
}
