package greencity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "user_location")
@Builder
public class UserLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_en")
    private String cityEn;

    @Column(name = "city_ua")
    private String cityUa;

    @Column(name = "region_en")
    private String regionEn;

    @Column(name = "region_ua")
    private String regionUa;

    @Column(name = "country_en")
    private String countryEn;

    @Column(name = "country_ua")
    private String countryUa;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @OneToMany(mappedBy = "userLocation", fetch = FetchType.LAZY)
    private List<User> users;
}
