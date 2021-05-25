package greencity.dto.rate;

import greencity.dto.user.UserForListDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstimateDto {
    private Long id;
    private Byte rate;
    private UserForListDto user;
}
