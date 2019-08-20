package greencity.dto.place;

import greencity.entity.enums.PlaceStatus;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceStatusDto {
    @NotNull Long id;
    @NotNull PlaceStatus status;
}
