package greencity.dto.habitstatistic;

import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitIdDto {
    @Valid List<@Valid Long> habitDictionaryId;
}

