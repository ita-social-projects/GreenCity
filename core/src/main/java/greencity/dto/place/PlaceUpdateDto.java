package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.dto.category.CategoryDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.location.LocationAddressAndGeoForUpdateDto;
import greencity.dto.openhours.OpeningHoursDto;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
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
    @Size(min = 1, message = ValidationConstants.BAD_OPENING_HOURS_LIST_REQUEST)
    private Set<OpeningHoursDto> openingHoursList;

    @Valid
    @Size(min = 1, message = ValidationConstants.BAD_DISCOUNT_VALUES_LIST_REQUEST)
    private Set<DiscountValueDto> discountValues;
}
