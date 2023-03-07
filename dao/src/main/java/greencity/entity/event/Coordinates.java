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
    private String streetEn;

    @Column
    private String streetUa;

    @Column
    private String houseNumber;

    @Column
    private String cityEn;

    @Column
    private String cityUa;

    @Column
    private String regionEn;

    @Column
    private String regionUa;

    @Column
    private String countryEn;

    @Column
    private String countryUa;
}