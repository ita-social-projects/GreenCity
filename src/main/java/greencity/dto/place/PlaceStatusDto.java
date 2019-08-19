package greencity.dto.place;

import greencity.entities.enums.PlaceStatus;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class PlaceStatusDto {
    @NotNull Long id;
    @NotNull PlaceStatus status;
}
