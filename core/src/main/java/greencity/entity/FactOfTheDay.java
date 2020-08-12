package greencity.entity;

import greencity.entity.enums.FactOfDayStatus;
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
@EqualsAndHashCode(exclude = {"factOfTheDayTranslations","createDate"})
@Table(name = "fact_of_the_day")
public class FactOfTheDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 300)
    private String title;

    @Enumerated(value = EnumType.ORDINAL)
    private FactOfDayStatus factOfDayStatus;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, mappedBy = "factOfTheDay", fetch = FetchType.LAZY)
    private List<FactOfTheDayTranslation> factOfTheDayTranslations;

    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;
}
