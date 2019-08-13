package greencity.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(nullable = false, unique = true, length = 50)
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

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "place")
    private List<Rate> rates = new ArrayList<>();

    @OneToMany(mappedBy = "place")
    private List<OpeningHours> openingHours = new ArrayList<>();

    private Boolean isApproved = false;
}