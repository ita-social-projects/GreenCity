package greencity.dto.advice;

import greencity.constant.ValidationConstants;
import greencity.dto.habit.HabitDto;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AdviceDto {
    @Min(1)
    private Long id;

    @NotBlank
    @Length(min = ValidationConstants.ADVICE_MIN_LENGTH, max = ValidationConstants.ADVICE_MAX_LENGTH)
    private String content;

    private HabitDto habit;
}
