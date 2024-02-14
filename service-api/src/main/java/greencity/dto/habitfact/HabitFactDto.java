package greencity.dto.habitfact;

import greencity.constant.ServiceValidationConstants;
import greencity.dto.habit.HabitDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
