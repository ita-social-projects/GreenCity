package greencity.dto.goal;

import greencity.dto.habitfact.HabitFactTranslationDto;
import greencity.entity.localization.GoalTranslation;
import lombok.*;

import java.util.List;

@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalResponseDto {
    private Long id;

    private List<GoalTranslationDTO> translations;
}
