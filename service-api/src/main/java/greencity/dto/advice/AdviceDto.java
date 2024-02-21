package greencity.dto.advice;

import greencity.constant.ServiceValidationConstants;
import greencity.dto.habit.HabitDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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
    @Size(min = ServiceValidationConstants.ADVICE_MIN_LENGTH, max = ServiceValidationConstants.ADVICE_MAX_LENGTH)
    private String content;

    private HabitDto habit;
}
