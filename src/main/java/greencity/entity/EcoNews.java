package greencity.entity;

import greencity.entity.localization.EcoNewsTranslation;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.*;

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
    private String text;

    @Column(nullable = false)
    private String imagePath;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ecoNews", cascade = {CascadeType.REMOVE, CascadeType.PERSIST,
        CascadeType.REFRESH})
    private List<EcoNewsTranslation> translations;
}