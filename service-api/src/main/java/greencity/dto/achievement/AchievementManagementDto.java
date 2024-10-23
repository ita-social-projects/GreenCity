package greencity.dto.achievement;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AchievementManagementDto extends AchievementPostDto {
    @NotNull
    @Min(1)
    private Long id;
}
