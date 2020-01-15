package greencity.entity.localization;

import greencity.entity.EcoNews;
import greencity.entity.Language;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "eco_news_translations")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EcoNewsTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Language language;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    private EcoNews ecoNews;
}

