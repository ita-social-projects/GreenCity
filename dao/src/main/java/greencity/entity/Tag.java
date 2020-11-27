package greencity.entity;

import java.util.List;
import java.util.Set;
import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "tags")
@ToString(exclude = {"ecoNews", "tipsAndTricks", "habits"})
@EqualsAndHashCode(exclude = {"ecoNews", "tipsAndTricks", "habits"})
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<EcoNews> ecoNews;

    @ManyToMany(mappedBy = "tags")
    private List<TipsAndTricks> tipsAndTricks;

    @ManyToMany(mappedBy = "tags")
    private Set<Habit> habits;
}
