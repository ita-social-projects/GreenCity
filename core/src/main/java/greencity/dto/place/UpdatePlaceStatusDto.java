package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.enums.PlaceStatus;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
