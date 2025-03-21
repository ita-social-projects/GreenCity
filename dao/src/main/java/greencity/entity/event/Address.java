package greencity.entity.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
public final class Address {
    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private String streetEn;

    @Column(name = "street_uk")
    private String streetUk;

    @Column
    private String houseNumber;

    @Column
    private String cityEn;

    @Column(name = "city_uk")
    private String cityUk;

    @Column
    private String regionEn;

    @Column(name = "region_uk")
    private String regionUk;

    @Column
    private String countryEn;

    @Column(name = "country_uk")
    private String countryUk;

    @Column
    private String formattedAddressEn;

    @Column(name = "formatted_address_uk")
    private String formattedAddressUk;
}