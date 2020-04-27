package greencity.dto.habitstatistic;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HabitCreateDto {
    @NotNull
    @Min(value = 1, message = "Habit id must be a positive number")
    private Long id;
    @NotEmpty(message = "HabitDictionary object must not be null")
    private HabitDictionaryDto habitDictionary;
    @NotEmpty(message = "Status integer must not be null")
    private Boolean status;
}
