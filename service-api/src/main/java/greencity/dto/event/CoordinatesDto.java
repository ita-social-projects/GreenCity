package greencity.dto.event;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public final class CoordinatesDto {
    @NotEmpty
    private double latitude;

    @NotEmpty
    private double longitude;
}
