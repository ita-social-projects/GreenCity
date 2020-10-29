package greencity.dto.place;

import greencity.enums.PlaceStatus;
import greencity.validator.ValidationConstants;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UpdatePlaceStatusDto {
    @Positive(message = ValidationConstants.NEGATIVE_ID)
    @NotNull
    private Long id;

    @NotNull
    private PlaceStatus status;
}
