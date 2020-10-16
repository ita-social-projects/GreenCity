package greencity.dto.habit;

import greencity.dto.habittranslation.HabitTranslationDto;
import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
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
public class HabitDto implements Serializable {
    private Long id;

    private String image;
    @Valid
    private List<HabitTranslationDto> habitTranslations;
}
