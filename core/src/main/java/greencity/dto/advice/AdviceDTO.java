package greencity.dto.advice;

import greencity.constant.ValidationConstants;
import greencity.dto.user.HabitDictionaryDto;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AdviceDTO {
    @Min(1)
    private Long id;

    @NotBlank(message = ValidationConstants.EMPTY_ADVICE)
    @Length(min = ValidationConstants.ADVICE_MIN_LENGTH, max = ValidationConstants.ADVICE_MAX_LENGTH,
        message = ValidationConstants.INVALID_ADVICE_LENGTH)
    private String content;

    private HabitDictionaryDto habitDictionary;
}
