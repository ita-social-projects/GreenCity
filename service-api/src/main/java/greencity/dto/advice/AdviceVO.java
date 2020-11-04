package greencity.dto.advice;

import greencity.dto.user.HabitIdRequestDto;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdviceVO {
    private Long id;

    private List<AdviceTranslationVO> translations;

    private HabitIdRequestDto habit;
}
