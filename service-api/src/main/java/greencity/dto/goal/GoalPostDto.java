package greencity.dto.goal;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.language.LanguageTranslationDTO;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class GoalPostDto {
    @LanguageTranslationConstraint
    private List<LanguageTranslationDTO> translations;

    @NotNull
    private GoalRequestDto goal;
}