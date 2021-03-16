package greencity.dto.habit;

import greencity.constant.AppConstant;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignPropertiesDto {
    @NotNull
    @Min(AppConstant.MIN_DAYS_DURATION_OF_HABIT_ASSIGN_FOR_USER)
    @Max(AppConstant.MAX_DAYS_DURATION_OF_HABIT_ASSIGN_FOR_USER)
    private Integer duration;
}
