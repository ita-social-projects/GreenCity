package greencity.entity;

import greencity.entity.enums.FactOfDayStatus;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "fact_of_the_day_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class FactOfTheDayTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Language language;

    @ManyToOne
    private FactOfTheDay factOfTheDay;

    @Column(nullable = false, unique = true, length = 300)
    private String content;
}
