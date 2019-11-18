package greencity.dto.advice;

import greencity.constant.ValidationConstants;
import greencity.entity.Advice;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class AdviceDto {
    @NotBlank(message = ValidationConstants.EMPTY_ADVICE)
    @Length(min = ValidationConstants.ADVICE_MIN_LENGTH,
        max = ValidationConstants.ADVICE_MAX_LENGTH,
        message = ValidationConstants.INVALID_ADVICE_LENGTH)
    private String name;

    /**
     * The constructor takes {@link Advice} parameter.
     *
     * @param advice {@link Advice}
     * @author Vitaliy Dzen
     */
    public AdviceDto(Advice advice) {
        this.name = advice.getName();
    }
}
