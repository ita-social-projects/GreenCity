package greencity.dto.habittranslation;

import greencity.dto.language.LanguageDTO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitTranslationDto {
    private Long id;
    private String name;
    private String habitItem;
    private String description;
    private LanguageDTO language;
}
