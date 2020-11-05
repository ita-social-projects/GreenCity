package greencity.dto.place;

import greencity.constant.ServiceValidationConstants;
import greencity.enums.PlaceStatus;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BulkUpdatePlaceStatusDto {
    @Valid
    private List<
        @Positive(message = ServiceValidationConstants.NEGATIVE_ID)
        @NotNull
            Long> ids;

    @NotNull
    private PlaceStatus status;
}
