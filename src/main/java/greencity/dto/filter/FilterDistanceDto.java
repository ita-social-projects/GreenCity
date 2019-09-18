package greencity.dto.filter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDistanceDto {
    @Min(value = -90, message = "Has to be greatest or equals -90")
    @Max(value = 90, message = "Has to be lover or equals 90")
    private Double lat;
    @Min(value = -180, message = "Has to be greatest or equals -180")
    @Max(value = 180, message = "Has to be lover or equals 180")
    private Double lng;
    @Positive
    private Double distance;
}