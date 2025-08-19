package greencity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @Column(name = "city_uk")
    private String cityUk;

    @Column(name = "region_en")
    private String regionEn;

    @Column(name = "region_uk")
    private String regionUk;

    @Column(name = "country_en")
    private String countryEn;

    @Column(name = "country_uk")
    private String countryUk;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @ToString.Exclude
    @OneToMany(mappedBy = "userLocation", fetch = FetchType.LAZY)
    private List<User> users;
}
