package greencity.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"specificationValues"})
@ToString(exclude = {"specificationValues"})
public class Specification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @OneToMany(mappedBy = "specification")
    private List<SpecificationValue> specificationValues = new ArrayList<>();

    @OneToMany(mappedBy = "specification", cascade = CascadeType.PERSIST)
    private List<Discount> discounts = new ArrayList<>();
}
