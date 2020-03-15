package greencity.entity;

import java.util.List;
import javax.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tags")
@ToString(exclude = "ecoNews")
@EqualsAndHashCode(exclude = "ecoNews")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<EcoNews> ecoNews;
}
