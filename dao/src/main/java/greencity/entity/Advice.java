package greencity.entity;

import greencity.entity.localization.AdviceTranslation;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "advice", fetch = FetchType.LAZY)
    private List<AdviceTranslation> translations;

    @ManyToOne(fetch = FetchType.LAZY)
    private Habit habit;
}
