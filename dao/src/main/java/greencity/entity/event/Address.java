package greencity.entity.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
public final class Address {
    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private String streetEn;

    @Column(nullable = false)
    private String streetUa;

    @Column
    private String houseNumber;

    @Column
    private String cityEn;

    @Column(nullable = false)
    private String cityUa;

    @Column
    private String regionEn;

    @Column(nullable = false)
    private String regionUa;

    @Column
    private String countryEn;

    @Column(nullable = false)
    private String countryUa;
}