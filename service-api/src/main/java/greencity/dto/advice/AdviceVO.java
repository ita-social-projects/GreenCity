package greencity.dto.advice;

import greencity.dto.user.HabitIdRequestDto;
import lombok.*;

import java.util.List;

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
