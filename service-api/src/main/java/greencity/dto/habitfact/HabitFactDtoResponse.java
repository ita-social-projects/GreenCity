package greencity.dto.habitfact;

import greencity.dto.habit.HabitVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitFactDtoResponse {
    private Long id;

    private List<HabitFactTranslationDto> translations;

    private HabitVO habit;
}
