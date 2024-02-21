package greencity.dto.habitfact;

import greencity.dto.habit.HabitVO;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
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
