package greencity.entity;

import greencity.entity.localization.EcoNewsTranslation;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "eco_news")
@ToString(exclude = {"translations", "author", "tags"})
@EqualsAndHashCode(exclude = {"translations", "author", "tags"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EcoNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private ZonedDateTime creationDate;

    @Column(nullable = false)
    private String imagePath;

    @ManyToOne
    private User author;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ecoNews", cascade = {CascadeType.REMOVE, CascadeType.PERSIST,
        CascadeType.REFRESH})
    private List<EcoNewsTranslation> translations;

    @ManyToMany
    private List<Tag> tags;
}