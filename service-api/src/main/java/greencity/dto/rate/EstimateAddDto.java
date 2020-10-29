package greencity.dto.rate;

import greencity.validator.ValidationConstants;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The data transfer object of the Estimate.
 *
 * @author Marian Milian
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstimateAddDto {
    @Min(value = 1, message = ValidationConstants.RATE_MIN_VALUE)
    @Max(value = 5, message = ValidationConstants.RATE_MAX_VALUE)
    private Byte rate;
}
