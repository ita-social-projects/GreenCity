package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.PlaceStatus;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDto {
    @NotNull(message = ValidationConstants.EMPTY_ID)
    private Long id;

    @NotNull(message = ValidationConstants.EMPTY_STATUS)
    private PlaceStatus status;
}
