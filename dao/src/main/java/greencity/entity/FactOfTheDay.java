package greencity.entity;

import java.time.ZonedDateTime;
import java.util.List;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"factOfTheDayTranslations", "createDate"})
@Table(name = "fact_of_the_day")
public class FactOfTheDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 300)
    private String name;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, mappedBy = "factOfTheDay", fetch = FetchType.LAZY)
    private List<FactOfTheDayTranslation> factOfTheDayTranslations;

    @CreationTimestamp
    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;
}
