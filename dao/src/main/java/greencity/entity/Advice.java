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

    public void addAdviceTranslation(AdviceTranslation adviceTranslation) {
        translations.add(adviceTranslation);
        adviceTranslation.setAdvice(this);
    }

    public void removeAdviceTranslation(AdviceTranslation adviceTranslation) {
        translations.remove(adviceTranslation);
        adviceTranslation.setAdvice(null);
    }
}
