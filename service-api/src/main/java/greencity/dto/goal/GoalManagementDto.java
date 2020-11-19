package greencity.dto.goal;

import greencity.enums.UserStatus;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class GoalManagementDto {
    @NotNull
    private Long id;

    private List<GoalTranslationVO> translations;
}
