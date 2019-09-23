package greencity.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"value", "place", "category"})
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int value;

    @ManyToOne
    @JsonBackReference
    private Place place;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Specification specification;
}
