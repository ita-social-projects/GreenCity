package greencity.entity;

import greencity.entity.enums.PlaceStatus;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(
    exclude = {"discountValues", "author", "openingHoursList", "comments", "photos",
        "location", "favoritePlaces", "category", "webPages", "status", "discountValues"})
@ToString(
    exclude = {"discountValues", "author", "openingHoursList", "comments", "photos",
        "location", "favoritePlaces", "category", "webPages", "status", "discountValues"}
)
@Table(name = "places")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private String description;

    @Column(unique = true, length = 15)
    private String phone;

    @Column(unique = true, length = 50)
    private String email;

    @OneToMany(mappedBy = "place", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.PERSIST)
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.PERSIST)
    private Set<DiscountValue> discountValues = new HashSet<>();

    @OneToOne(cascade = {CascadeType.ALL})
    private Location location;

    @OneToMany(mappedBy = "place")
    private List<FavoritePlace> favoritePlaces = new ArrayList<>();

    @ManyToMany(mappedBy = "places")
    private List<WebPage> webPages = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.ALL})
    private Category category;

    @OneToMany(mappedBy = "place")
    private List<Estimate> estimates = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<OpeningHours> openingHoursList = new HashSet<>();

    @ManyToOne
    private User author;

    @Column(name = "modified_date")
    private ZonedDateTime modifiedDate;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "status")
    private PlaceStatus status = PlaceStatus.PROPOSED;
}
