package greencity.dto.habit;

import greencity.constant.AppConstant;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignPropertiesDto {
    @NotNull
    @Range(min = AppConstant.MIN_DAYS_DURATION_OF_HABIT_ASSIGN_FOR_USER,
        max = AppConstant.MAX_DAYS_DURATION_OF_HABIT_ASSIGN_FOR_USER)
    private Integer duration;
}
