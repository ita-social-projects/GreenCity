package greencity.dto.location;

import greencity.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class LocationDto {

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    private String address;

    public LocationDto(Location location) {
        this.lat = location.getLat();
        this.lng = location.getLng();

    }
}
