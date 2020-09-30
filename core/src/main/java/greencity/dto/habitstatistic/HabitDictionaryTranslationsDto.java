package greencity.dto.habitstatistic;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HabitDictionaryTranslationsDto {
    private String name;

    private String description;

    private String habitItem;
}
