package greencity.dto.filter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static greencity.constant.ServiceValidationConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDistanceDto {
    @Min(value = -90, message = LAT_MIN_VALIDATION)
    @Max(value = 90, message = LAT_MAX_VALIDATION)
    private Double lat;
    @Min(value = -180, message = LNG_MIN_VALIDATION)
    @Max(value = 180, message = LNG_MAX_VALIDATION)
    private Double lng;
    @Positive
    private Double distance;
}
