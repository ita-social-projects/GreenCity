package greencity.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import greencity.constant.AppConstant;
import greencity.entity.enums.PlaceStatus;
import greencity.util.DateTimeService;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.*;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(
    exclude = {"comments", "photos", "location", "favoritePlaces", "category", "rates", "webPages", "status"})
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

    @OneToMany(mappedBy = "place")
    @JsonManagedReference
    private List<OpeningHours> openingHoursList = new ArrayList<>();

    @ManyToOne private User author;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate = DateTimeService.getDateTime(AppConstant.UKRAINE_TIMEZONE);

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "status")
    private PlaceStatus status = PlaceStatus.PROPOSED;
}
