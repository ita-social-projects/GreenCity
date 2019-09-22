package greencity.dto.rate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import greencity.entity.Rate;
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
    private Byte rate;
    @JsonIgnore
    private Long place;
    @JsonIgnore
    private Long user;
}
