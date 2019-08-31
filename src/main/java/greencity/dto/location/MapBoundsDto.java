package greencity.dto.location;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapBoundsDto {
    @Min(value = -90, message = "The latitude must be at least -90")
    @Max(value = 90, message = "The latitude must be less than 90")
    @NotNull(message = "North-east latitude can not be null")
    private Double northEastLat;

    @Min(value = -180, message = "The latitude must be at least -180")
    @Max(value = 180, message = "The latitude must be less than 180")
    @NotNull(message = "North-east longitude can not be null")
    private Double northEastLng;

    @Min(value = -90, message = "The latitude must be at least -90")
    @Max(value = 90, message = "The latitude must be less than 90")
    @NotNull(message = "South-west latitude can not be null")
    private Double southWestLat;

    @Min(value = -180, message = "The latitude must be at least -180")
    @Max(value = 180, message = "The latitude must be less than 180")
    @NotNull(message = "South-west longitude can not be null")
    private Double southWestLng;
}
