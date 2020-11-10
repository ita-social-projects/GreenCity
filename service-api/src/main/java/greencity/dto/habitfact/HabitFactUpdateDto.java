package greencity.dto.habitfact;

import greencity.dto.user.HabitIdRequestDto;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HabitFactUpdateDto {
    @NotNull
    private List<HabitFactTranslationUpdateDto> translations;

    @NotNull
    private HabitIdRequestDto habit;
}
