package greencity.dto.habitstatistic;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HabitDictionaryDto {
    @NotNull
    @Min(value = 1, message = "HabitDictionary id must be a positive number")
    private Long id;

    @NotEmpty(message = "name habitDictionary must not be null")
    private String name;

    @NotEmpty(message = "description habitDictionary must not be null")
    private String description;

    @NotEmpty
    private String image;
}
