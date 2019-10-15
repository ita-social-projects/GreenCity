package greencity.dto.rate;

import greencity.constant.ValidationConstants;
import greencity.entity.Rate;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The data transfer object of the {@link Rate}.
 *
 * @author Marian Milian
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateAddDto {
    @Size(min = 1, max = 5, message = ValidationConstants.RATE_MAX_VALUE)
    private Byte rate;
}
