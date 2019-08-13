package greencity.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToOne private Category category;

    @OneToMany(mappedBy = "category")
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<Place> places = new ArrayList<>();
}
