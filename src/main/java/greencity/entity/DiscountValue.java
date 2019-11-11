package greencity.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"value"})
@Table(name = "discount_values")
public class DiscountValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private Integer value;

    @ManyToOne
    private Place place;

    @ManyToOne
    private Specification specification;
}
