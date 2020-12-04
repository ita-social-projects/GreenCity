package greencity.entity;

import java.util.List;
import java.util.Set;
import javax.persistence.*;

import greencity.entity.localization.TagTranslation;
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

    @OneToMany(mappedBy = "tag")
    private List<TagTranslation> tagTranslations;

    @ManyToMany(mappedBy = "tags")
    private List<EcoNews> ecoNews;

    @ManyToMany(mappedBy = "tags")
    private List<TipsAndTricks> tipsAndTricks;

    @ManyToMany(mappedBy = "tags")
    private Set<Habit> habits;
}
