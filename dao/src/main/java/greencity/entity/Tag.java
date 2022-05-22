package greencity.entity;

import java.util.List;
import java.util.Set;
import javax.persistence.*;

import greencity.entity.localization.TagTranslation;
import greencity.enums.TagType;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "tags")
@ToString(exclude = {"ecoNews", "tipsAndTricks", "habits", "events"})
@EqualsAndHashCode(exclude = {"ecoNews", "tipsAndTricks", "habits", "events"})
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TagType type;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    private List<TagTranslation> tagTranslations;

    @ManyToMany(mappedBy = "tags")
    private List<EcoNews> ecoNews;

    @ManyToMany(mappedBy = "tags")
    private List<TipsAndTricks> tipsAndTricks;

    @ManyToMany(mappedBy = "tags")
    private Set<Habit> habits;

    @ManyToMany(mappedBy = "tags")
    private Set<Event> events;
}
