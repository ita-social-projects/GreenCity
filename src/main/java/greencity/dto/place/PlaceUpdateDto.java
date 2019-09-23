package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.dto.category.CategoryDto;
import greencity.dto.discount.DiscountDtoForUpdatePlace;
import greencity.dto.location.LocationAddressAndGeoForUpdateDto;
import greencity.dto.openhours.OpeningHoursUpdateDto;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceUpdateDto {
    @NotNull
    private Long id;

    @NotBlank(message = ValidationConstants.EMPTY_PLACE_NAME)
    @Length(max = ValidationConstants.PLACE_NAME_MAX_LENGTH)
    private String name;

    @Valid
    private LocationAddressAndGeoForUpdateDto location;

    @Valid
    private CategoryDto category;

    @Valid
    private Set<OpeningHoursUpdateDto> openingHoursList;

    @Valid
    private Set<DiscountDtoForUpdatePlace> discounts;
}
