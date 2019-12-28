package greencity.dto.habitstatistic;

import greencity.dto.user.HabitDictionaryDto;
import java.time.ZonedDateTime;
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
    private String name;
    private Boolean status;
    private String description;
    private String habitName;
    private String habitItem;
    private ZonedDateTime createDate;
    private List<HabitStatisticDto> habitStatistics;
    private HabitDictionaryDto habitDictionary;
}
