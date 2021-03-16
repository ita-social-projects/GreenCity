package greencity.dto.habitfact;

import greencity.constant.ServiceValidationConstants;
import greencity.dto.habit.HabitDto;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitFactDto {
    @Min(1)
    private Long id;

    @NotBlank
    @Size(min = ServiceValidationConstants.HABIT_FACT_MIN_LENGTH,
        max = ServiceValidationConstants.HABIT_FACT_MAX_LENGTH)
    private String content;

    @NotNull
    private HabitDto habit;
}
