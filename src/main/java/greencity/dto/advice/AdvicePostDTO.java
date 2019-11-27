package greencity.dto.advice;

import greencity.constant.ValidationConstants;
import greencity.entity.Advice;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class AdvicePostDTO {
    @NotBlank(message = ValidationConstants.EMPTY_ADVICE)
    @Length(min = ValidationConstants.ADVICE_MIN_LENGTH, max = ValidationConstants.ADVICE_MAX_LENGTH,
        message = ValidationConstants.INVALID_ADVICE_LENGTH)
    private String advice;

    @Min(1)
    private Long habitDictionaryId;

    /**
     * The constructor takes {@link Advice} parameter.
     *
     * @param advice {@link Advice}
     * @author Vitaliy Dzen
     */
    public AdvicePostDTO(Advice advice) {
        this.advice = advice.getName();
        this.habitDictionaryId = advice.getHabitDictionary().getId();
    }
}
