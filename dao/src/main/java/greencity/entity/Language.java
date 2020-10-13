package greencity.entity;

import greencity.entity.localization.AdviceTranslation;
import greencity.entity.localization.GoalTranslation;
import java.util.List;
import javax.persistence.*;
import lombok.*;


@Entity
@Table(name = "languages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"adviceTranslations", "goalTranslations", "habitTranslations"})
@ToString(exclude = {"adviceTranslations", "goalTranslations", "habitTranslations"})
@Builder
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 35)
    private String code;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<AdviceTranslation> adviceTranslations;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<HabitTranslation> habitTranslations;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<GoalTranslation> goalTranslations;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<FactOfTheDayTranslation> factOfTheDayTranslations;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<TitleTranslation> titleTranslations;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<TextTranslation> textTranslations;
}