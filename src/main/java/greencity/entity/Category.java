package greencity.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"categories", "places"})
@ToString(exclude = {"categories", "places"})
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToOne private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<Place> places = new ArrayList<>();
}
