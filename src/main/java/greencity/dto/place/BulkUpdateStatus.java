package greencity.dto.place;

import greencity.entity.enums.PlaceStatus;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateStatus {
    @NotNull
    private PlaceStatus status;

    @NotNull
    private Long updatedPlaces;
}
