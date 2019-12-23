package greencity.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "advice_translations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdviceTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Language language;

    @OneToMany(mappedBy = "translations")
    private Advice advice;

    @Column(name = "name", nullable = false, unique = true, length = 300)
    private String content;
}
