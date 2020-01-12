package greencity.entity;

import greencity.entity.localization.EcoNewsTranslation;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EcoNews ecoNews = (EcoNews) o;
        return id.equals(ecoNews.id)
            && creationDate.equals(ecoNews.creationDate)
            && text.equals(ecoNews.text)
            && imagePath.equals(ecoNews.imagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate, text, imagePath);
    }
}
