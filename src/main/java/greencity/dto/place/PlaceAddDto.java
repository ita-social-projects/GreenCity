package greencity.dto.place;

import greencity.dto.location.LocationDto;
import greencity.dto.openingHours.OpeningHoursDto;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@RequiredArgsConstructor
public class PlaceAddDto {

    @NotBlank
    @Length(max = 30)
    private String name;

    @NotBlank
    @Length(max = 30)
    private String address;

    @NotNull private LocationDto locationDto;

    @NotNull private Long categoryId;

    @NotNull private Long authorId;

    @NotNull private List<OpeningHoursDto> openingHoursDtoList;

    @NotNull private PlaceStatus placeStatus = PlaceStatus.PROPOSED;


}
