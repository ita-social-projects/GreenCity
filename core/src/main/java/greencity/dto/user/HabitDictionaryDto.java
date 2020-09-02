package greencity.dto.user;

import greencity.entity.HabitDictionaryTranslation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HabitDictionaryDto {
    private Long id;
    private String name;
    private String habitItem;
    private String description;
    private String image;
    private List<HabitDictionaryTranslation> habitDictionaryTranslations;
}
