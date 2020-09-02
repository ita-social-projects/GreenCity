package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BulkUpdatePlaceStatusDto {
    @Valid
    private List<
        @Positive(message = ValidationConstants.NEGATIVE_ID)
        @NotNull(message = ValidationConstants.EMPTY_ID)
            Long> ids;

    @NotNull(message = ValidationConstants.EMPTY_STATUS)
    private PlaceStatus status;
}
