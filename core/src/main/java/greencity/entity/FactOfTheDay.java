package greencity.entity;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@Table(name = "fact_of_the_day")
public class FactOfTheDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, mappedBy = "factOfTheDay", fetch = FetchType.LAZY)
    private List<FactOfTheDayTranslation> factOfTheDayTranslations;

    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;
}
