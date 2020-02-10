package greencity.entity.localization;

import greencity.entity.Advice;
import greencity.entity.Language;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "advice_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "advice")
@ToString(exclude = "advice")
@Builder
public class AdviceTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Language language;

    @ManyToOne
    private Advice advice;

    @Column(nullable = false, unique = true, length = 300)
    private String content;
}
