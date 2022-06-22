package greencity.entity.event;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Embeddable
@ToString
@EqualsAndHashCode
public final class Coordinates {
    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private String addressUa;

    @Column
    private String addressEn;
}
