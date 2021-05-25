package greencity.dto.place;

import greencity.constant.ServiceValidationConstants;
import greencity.enums.PlaceStatus;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UpdatePlaceStatusDto {
    @Positive(message = ServiceValidationConstants.NEGATIVE_ID)
    @NotNull
    private Long id;

    @NotNull
    private PlaceStatus status;
}
