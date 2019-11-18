package greencity.dto.advice;

import greencity.constant.ValidationConstants;
import greencity.entity.Advice;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class AdviceAdminDTO {
    @NotNull
    private Long id;

    @NotBlank(message = ValidationConstants.EMPTY_ADVICE)
    @Length(min = ValidationConstants.ADVICE_MIN_LENGTH, max = ValidationConstants.ADVICE_MAX_LENGTH,
        message = ValidationConstants.INVALID_ADVICE_LENGTH)
    private String name;

    @NotNull
    private Long habitDictionaryId;

    /**
     * The constructor takes {@link Advice} parameter.
     *
     * @param advice {@link Advice}
     * @author Vitaliy Dzen
     */
    public AdviceAdminDTO(Advice advice) {
        this.id = advice.getId();
        this.name = advice.getName();
        this.habitDictionaryId = advice.getHabitDictionary().getId();
    }
}
