package greencity.dto.habittranslation;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitTranslationDto {
    private String name;
    private String habitItem;
    private String description;
    private String languageCode;
}
