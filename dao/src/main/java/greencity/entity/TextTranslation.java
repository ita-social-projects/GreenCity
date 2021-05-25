package greencity.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "text_translations")
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "tipsAndTricks")
@NoArgsConstructor
public class TextTranslation extends Translation {
    @Getter
    @Setter
    @ManyToOne
    private TipsAndTricks tipsAndTricks;
}
