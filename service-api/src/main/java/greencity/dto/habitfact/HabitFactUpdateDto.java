package greencity.dto.habitfact;

import greencity.dto.user.HabitIdRequestDto;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
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
