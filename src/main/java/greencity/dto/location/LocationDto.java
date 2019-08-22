package greencity.dto.location;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

}
