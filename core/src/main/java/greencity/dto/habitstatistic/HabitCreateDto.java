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
    @Min(1)
    private Long id;
    @NotEmpty
    private HabitDictionaryDto habitDictionary;
    @NotEmpty
    private Boolean status;
}
