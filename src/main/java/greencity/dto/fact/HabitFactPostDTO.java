package greencity.dto.fact;

import greencity.constant.ValidationConstants;
import greencity.dto.user.HabitDictionaryIdDto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitFactPostDTO {
    @NotBlank(message = ValidationConstants.EMPTY_HABIT_FACT)
    @Length(min = ValidationConstants.HABIT_FACT_MIN_LENGTH, max = ValidationConstants.HABIT_FACT_MAX_LENGTH,
        message = ValidationConstants.INVALID_HABIT_FACT_LENGTH)
    private String fact;

    @NotNull(message = "habitDictionary can not be null")
    private HabitDictionaryIdDto habitDictionary;
}
