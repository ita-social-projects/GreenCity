package greencity.entity;

import greencity.entity.localization.AdviceTranslation;
import greencity.entity.localization.ShoppingListItemTranslation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "languages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"adviceTranslations", "shoppingListItemTranslations", "habitTranslations"})
@ToString(exclude = {"adviceTranslations", "shoppingListItemTranslations", "habitTranslations"})
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
    private List<ShoppingListItemTranslation> shoppingListItemTranslations;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<FactOfTheDayTranslation> factOfTheDayTranslations;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<TitleTranslation> titleTranslations;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<TextTranslation> textTranslations;
}
