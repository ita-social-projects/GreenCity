package greencity.dto.place;

import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private String name;

    @NotBlank
    private String address;

    @NotNull
    private Long categoryId;

    @Size(min = 1, message = ServiceValidationConstants.BAD_OPENING_HOURS_LIST_REQUEST)
    private Set<OpeningHoursDto> openingHoursList = new HashSet<>();
}
