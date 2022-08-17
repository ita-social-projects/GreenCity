package greencity.dto.event;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@Setter
public class CoordinatesDto {
    private double latitude;
    private double longitude;
    private String addressUa;
    private String addressEn;
}
