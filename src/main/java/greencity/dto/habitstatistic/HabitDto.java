package greencity.dto.habitstatistic;

import greencity.dto.user.HabitDictionaryDto;
import java.time.LocalDate;
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
    private String habitItem;
    private LocalDate createDate;
    private List<HabitStatisticDto> habitStatistics;
    private HabitDictionaryDto habitDictionary;
}
