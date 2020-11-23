package greencity.dto.achievement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AchievementManagementDto extends AchievementPostDto {
    @NotNull
    @Min(1)
    private Long id;
}
