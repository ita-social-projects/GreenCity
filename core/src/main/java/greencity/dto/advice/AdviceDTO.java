package greencity.dto.advice;

import greencity.constant.ValidationConstants;
import greencity.dto.user.HabitDictionaryDto;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AdviceDTO {
    @Min(1)
    private Long id;

    @NotBlank
    @Length(min = ValidationConstants.ADVICE_MIN_LENGTH, max = ValidationConstants.ADVICE_MAX_LENGTH)
    private String content;

    private HabitDictionaryDto habitDictionary;
}
