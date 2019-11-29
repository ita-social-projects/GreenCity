package greencity.dto.fact;

import greencity.constant.ValidationConstants;
import greencity.entity.HabitFact;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class HabitFactDTO {
    @Min(1)
    private Long id;

    @NotBlank(message = ValidationConstants.EMPTY_HABIT_FACT)
    @Length(min = ValidationConstants.HABIT_FACT_MIN_LENGTH, max = ValidationConstants.HABIT_FACT_MAX_LENGTH,
        message = ValidationConstants.INVALID_HABIT_FACT_LENGTH)
    private String fact;

    @Min(1)
    private Long habitDictionaryId;

    @NotNull(message = "habitDictionaryName can not be null")
    private String habitDictionaryName;

    /**
     * The constructor takes {@link HabitFact} parameter.
     *
     * @param habitFact {@link HabitFact}
     * @author Vitaliy Dzen
     */
    public HabitFactDTO(HabitFact habitFact) {
        this.id = habitFact.getId();
        this.fact = habitFact.getFact();
        this.habitDictionaryId = habitFact.getHabitDictionary().getId();
        this.habitDictionaryName = habitFact.getHabitDictionary().getName();
    }
}
