package greencity.entity;

import java.time.ZonedDateTime;
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
    private String title;

    @Column(nullable = false)
    private ZonedDateTime creationDate;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String imagePath;

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
            && title.equals(ecoNews.title)
            && creationDate.equals(ecoNews.creationDate)
            && text.equals(ecoNews.text)
            && imagePath.equals(ecoNews.imagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, creationDate, text, imagePath);
    }
}
