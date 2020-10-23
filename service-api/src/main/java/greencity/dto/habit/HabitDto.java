package greencity.dto.habit;

import greencity.dto.habittranslation.HabitTranslationDto;
import java.io.Serializable;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitDto implements Serializable {
    private Long id;
    private String image;
    private HabitTranslationDto habitTranslation;
}
