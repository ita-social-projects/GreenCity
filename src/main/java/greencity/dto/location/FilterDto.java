package greencity.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {
    private MapBoundsDto mapBoundsDto;

    private int discountMin;

    private int discountMax;
}
