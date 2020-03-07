package greencity.entity;

import greencity.entity.localization.EcoNewsTranslation;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "translations")
@ToString(exclude = "translations")
@Table(name = "eco_news")
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