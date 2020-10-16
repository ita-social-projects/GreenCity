package greencity.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "title_translations")
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "tipsAndTricks")
@NoArgsConstructor
public class TitleTranslation extends Translation {
    @Getter
    @Setter
    @ManyToOne
    private TipsAndTricks tipsAndTricks;
}
