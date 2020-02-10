package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.dto.category.CategoryDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.photo.PhotoAddDto;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceAddDto {
    @NotBlank(message = ValidationConstants.EMPTY_PLACE_NAME)
    @Length(max = ValidationConstants.PLACE_NAME_MAX_LENGTH)
    private String name;

    @Valid
    private LocationAddressAndGeoDto location;

    @Valid
    private CategoryDto category;

    @Valid
    @Size(min = 1, message = ValidationConstants.BAD_OPENING_HOURS_LIST_REQUEST)
    private Set<OpeningHoursDto> openingHoursList = new HashSet<>();

    @Valid
    @Size(min = 1, message = ValidationConstants.BAD_DISCOUNT_VALUES_LIST_REQUEST)
    private Set<DiscountValueDto> discountValues = new HashSet<>();

    @Valid
    @Size(max = 5, message = ValidationConstants.BAD_PHOTO_LIST_REQUEST)
    private List<PhotoAddDto> photos;
}
