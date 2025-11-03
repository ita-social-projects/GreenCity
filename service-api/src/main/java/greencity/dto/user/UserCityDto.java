package greencity.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCityDto {
    @NotNull
    private Long id;
    private String cityEn;
    private String cityUk;
    private Double latitude;
    private double longitude;
}
