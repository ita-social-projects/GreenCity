package greencity.dto.habitstatistic;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitIdDto {
    private List<Long> habitDictionaryId;
}

