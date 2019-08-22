package greencity.entity;

import greencity.entity.enums.PlaceStatus;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"name", "address", "comments", "photos", "location", "favoritePlaces", "category",
"openingHours", "rates", "webPages", "status"})
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @OneToMany(mappedBy = "place")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "place")
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "place")
    private List<SpecificationValue> specificationValues = new ArrayList<>();

    @OneToOne(mappedBy = "place")
    private Location location;

    @OneToMany(mappedBy = "place")
    private List<FavoritePlace> favoritePlaces = new ArrayList<>();

    @ManyToMany(mappedBy = "places")
    private List<WebPage> webPages = new ArrayList<>();

    @ManyToOne private Category category;

    @OneToMany(mappedBy = "place")
    private List<Rate> rates = new ArrayList<>();

    @ManyToOne
    private User author;

    @OneToMany(mappedBy = "place")
    private List<OpeningHours> openingHours = new ArrayList<>();

    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    private PlaceStatus status = PlaceStatus.PROPOSED;
}
