package greencity.dto.habit;

import greencity.dto.habittranslation.HabitTranslationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MutualHabitDto {
    private Long id;
    private String image;
    private HabitTranslationDto habitTranslation;
}
