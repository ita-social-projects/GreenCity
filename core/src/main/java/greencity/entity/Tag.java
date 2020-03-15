package greencity.entity;

import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
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
