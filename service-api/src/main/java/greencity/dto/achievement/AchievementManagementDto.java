package greencity.dto.achievement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class AchievementManagementDto extends AchievementPostDto {
    @NotNull
    @Min(1)
    private Long id;
}
