package greencity.dto.habit;

import greencity.dto.habittranslation.HabitTranslationManagementDto;
import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class HabitManagementDto implements Serializable {
    private Long id;
    private String image;
    @Valid
    private List<HabitTranslationManagementDto> habitTranslations;
}
