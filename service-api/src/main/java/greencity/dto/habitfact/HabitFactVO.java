package greencity.dto.habitfact;

import greencity.dto.habit.HabitVO;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HabitFactVO {
    private Long id;

    private List<HabitFactTranslationVO> translations;

    private HabitVO habit;
}
