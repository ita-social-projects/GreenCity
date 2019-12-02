package greencity.dto.habitstatistic;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitDictionaryDto {
    @NotNull
    @Min(value = 1, message = "HabitDictionary id must be a positive number")
    private Long id;
    @NotEmpty(message = "name habitDictionary must not be null")
    private String name;
    @NotEmpty(message = "description habitDictionary must not be null")
    private String description;
}
