package greencity.dto.habitfact;

import greencity.constant.ValidationConstants;
import greencity.dto.habit.HabitDto;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitFactDto {
    @Min(1)
    private Long id;

    @NotBlank
    @Length(min = ValidationConstants.HABIT_FACT_MIN_LENGTH, max = ValidationConstants.HABIT_FACT_MAX_LENGTH)
    private String content;

    @NotNull
    private HabitDto habit;
}
