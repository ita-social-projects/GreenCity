package greencity.dto.location;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapBoundsDto {
  @NotNull(message = "North east latitude can not be null")
  private Double northEastLat;

  @NotNull(message = "North east longitude can not be null")
  private Double northEastLng;

  @NotNull(message = "South west latitude can not be null")
  private Double southWestLat;

  @NotNull(message = "South west longitude can not be null")
  private Double southWestLng;
}
