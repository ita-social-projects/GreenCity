package greencity.dto.filter;

import greencity.constant.ServiceValidationConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDistanceDto {
    @Min(value = -90, message = ServiceValidationConstants.LAT_MIN_VALIDATION)
    @Max(value = 90, message = ServiceValidationConstants.LAT_MAX_VALIDATION)
    private Double lat;
    @Min(value = -180, message = ServiceValidationConstants.LNG_MIN_VALIDATION)
    @Max(value = 180, message = ServiceValidationConstants.LNG_MAX_VALIDATION)
    private Double lng;
    @Positive
    private Double distance;
}
