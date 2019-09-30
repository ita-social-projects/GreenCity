package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdatePlaceStatusDto {
    @NotNull(message = ValidationConstants.EMPTY_ID)
    private List<Long> ids;

    @NotNull(message = ValidationConstants.EMPTY_STATUS)
    private PlaceStatus status;
}
