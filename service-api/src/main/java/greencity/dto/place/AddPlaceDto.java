package greencity.dto.place;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import greencity.constant.ServiceValidationConstants;
import greencity.dto.openhours.OpeningHoursDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AddPlaceDto {
    @NotBlank
    @Length(max = ServiceValidationConstants.PLACE_NAME_MAX_LENGTH)
    private String placeName;

    @Valid
    private String categoryName;

    @Valid
    @Size(min = 1, message = ServiceValidationConstants.BAD_OPENING_HOURS_LIST_REQUEST)
    private Set<OpeningHoursDto> openingHoursList = new HashSet<>();

    private String locationName;
}
