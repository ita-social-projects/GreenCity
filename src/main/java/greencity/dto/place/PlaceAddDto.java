package greencity.dto.place;

import greencity.dto.location.LocationDto;
import greencity.dto.openingHours.OpeningHoursDto;
import greencity.entity.Place;

import greencity.entity.enums.PlaceStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class PlaceAddDto {

    @NotBlank
    @Length(max = 30)
    private String name;

    //add regex
    @NotBlank
    @Length(max = 30)
    private String address;

    private LocationDto locationDto;

    private List<OpeningHoursDto> openingHoursDtoList;

    private PlaceStatus placeStatus = PlaceStatus.PROPOSED;

    public PlaceAddDto(Place place) {
        this.name = place.getName();
        this.address = place.getAddress();
        this.locationDto = new LocationDto(place.getLocation());
        this.openingHoursDtoList = place.getOpeningHours()
            .stream().map(OpeningHoursDto::new)
            .collect(Collectors.toList());
        this.placeStatus = place.getStatus();
    }
}
