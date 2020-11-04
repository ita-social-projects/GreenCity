package greencity.dto.habit;

import greencity.dto.habittranslation.HabitTranslationManagementDto;
import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitManagementDto implements Serializable {
    private Long id;
    private String image;
    @Valid
    private List<HabitTranslationManagementDto> habitTranslations;
}
