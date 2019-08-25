package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.PlaceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class PlaceStatusDto {
    @NotNull(message = ValidationConstants.EMPTY_ID)
    Long id;

    @NotNull(message = ValidationConstants.EMPTY_STATUS)
    PlaceStatus status;
}
