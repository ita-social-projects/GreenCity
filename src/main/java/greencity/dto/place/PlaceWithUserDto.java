package greencity.dto.place;

import greencity.constant.ValidationConstants;
import greencity.dto.category.CategoryDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.user.UserForListDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceWithUserDto {

    private Long id;

    @NotBlank(message = ValidationConstants.EMPTY_PLACE_NAME)
    @Length(max = ValidationConstants.PLACE_NAME_MAX_LENGTH)
    private String name;

    @Valid private LocationAddressAndGeoDto location;

    @Valid private CategoryDto category;

    @Valid private List<OpeningHoursDto> openingHoursList;

    @Valid private UserForListDto author;
}
