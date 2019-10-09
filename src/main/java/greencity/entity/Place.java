package greencity.entity;

import greencity.constant.AppConstant;
import greencity.entity.enums.PlaceStatus;
import greencity.util.DateTimeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(
    exclude = {"discountValues", "author", "openingHoursList", "comments", "photos",
        "location", "favoritePlaces", "category", "rates", "webPages", "status", "discountValues"})
@ToString(exclude = {"comments", "photos", "favoritePlaces",
    "webPages", "rates", "discountValues", "openingHoursList", "location", "author"})
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

    @OneToMany(mappedBy = "place")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "place")
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
    private List<Rate> rates = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<OpeningHours> openingHoursList = new HashSet<>();

    @ManyToOne
    private User author;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate = DateTimeService.getDateTime(AppConstant.UKRAINE_TIMEZONE);

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "status")
    private PlaceStatus status = PlaceStatus.PROPOSED;
}
