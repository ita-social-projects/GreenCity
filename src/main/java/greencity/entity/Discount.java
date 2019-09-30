package greencity.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"value", "place", "category", "specification"})
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int value;

    @ManyToOne
    private Place place;

    @ManyToOne
    private Category category;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Specification specification;
}
