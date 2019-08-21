package greencity.dto.location;

import greencity.entity.Location;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class LocationDto {

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    public LocationDto(Location location) {
        this.lat = location.getLat();
        this.lng = location.getLng();

    }
}
