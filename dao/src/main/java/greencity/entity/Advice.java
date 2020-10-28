package greencity.entity;

import greencity.entity.localization.AdviceTranslation;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@Table(name = "advices")
public class Advice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(value = AccessLevel.PRIVATE)
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "advice", fetch = FetchType.LAZY)
    private List<AdviceTranslation> translations;

    @ManyToOne(fetch = FetchType.LAZY)
    private Habit habit;

    /**
     * Method that adds AdviceTranslation to Advice and also sets up a two-way relationship.
     *
     * @param adviceTranslation {@link AdviceTranslation}
     * @author Markiyan Derevetskyi
     */
    public void addAdviceTranslation(AdviceTranslation adviceTranslation) {
        translations.add(adviceTranslation);
        adviceTranslation.setAdvice(this);
    }

    /**
     * Method that removes AdviceTranslation from Advice and also sets up a two-way relationship.
     *
     * @param adviceTranslation {@link AdviceTranslation}
     * @author Markiyan Derevetskyi
     */
    public void removeAdviceTranslation(AdviceTranslation adviceTranslation) {
        translations.remove(adviceTranslation);
        adviceTranslation.setAdvice(null);
    }
}
