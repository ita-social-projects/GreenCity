package greencity.dto.location;

import greencity.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationByBoundsDto {
  private long placeId;
  private String placeName;
  private Double lat;
  private Double lng;

  public LocationByBoundsDto(Location location) {
    this.placeId = location.getPlace().getId();
    this.placeName = location.getPlace().getName();
    this.lat = location.getLat();
    this.lng = location.getLng();
  }
}
