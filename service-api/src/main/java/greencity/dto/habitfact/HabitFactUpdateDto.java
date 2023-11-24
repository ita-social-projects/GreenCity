package greencity.dto.habitfact;

import greencity.dto.user.HabitIdRequestDto;
import lombok.*;
import jakarta.validation.Valid;
import java.util.List;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HabitFactUpdateDto {
    @Valid
    private List<HabitFactTranslationUpdateDto> translations;

    @Valid
    private HabitIdRequestDto habit;
}
