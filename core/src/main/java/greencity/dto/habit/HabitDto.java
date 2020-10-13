package greencity.dto.habit;

import greencity.dto.habittranslation.HabitTranslationDto;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitDto {
    @NotNull
    @Min(1)
    private Long id;
    private String image;
    @NotEmpty
    private List<HabitTranslationDto> habitTranslations;
}
