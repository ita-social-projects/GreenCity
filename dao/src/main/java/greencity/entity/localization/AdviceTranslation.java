package greencity.entity.localization;

import greencity.entity.Advice;
import greencity.entity.Translation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "advice_translations")
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "advice")
@NoArgsConstructor
public class AdviceTranslation extends Translation {
    @Getter
    @Setter
    @ManyToOne
    private Advice advice;
}
