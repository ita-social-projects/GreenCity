package greencity.dto.habitstatistic;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitDto {
    private Long id;
    private String habitName;
    private String habitDescription;
    private List<HabitStatisticDto> habitStatistics;
}
