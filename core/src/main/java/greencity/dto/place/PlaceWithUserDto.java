package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.dto.category.CategoryDto;
import greencity.dto.discount.DiscountValueDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.photo.PhotoAddDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PlaceWithUserDto {
    private Long id;

    @NotBlank(message = ValidationConstants.EMPTY_PLACE_NAME)
    @Length(max = ValidationConstants.PLACE_NAME_MAX_LENGTH)
    private String name;

    @Valid
    private LocationAddressAndGeoDto location;

    @Valid
    private CategoryDto category;

    @Valid
    private List<OpeningHoursDto> openingHoursList;

    @Valid
    private List<DiscountValueDto> discountValues;

    @NotNull
    private PlaceStatus status;

    @Valid
    private PlaceAuthorDto author;

    @Valid
    private List<PhotoAddDto> photos;
}
