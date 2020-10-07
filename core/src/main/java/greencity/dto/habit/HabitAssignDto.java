package greencity.dto.habit;

import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatus.HabitStatusDto;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignDto {
    @NotNull
    @Min(1)
    private Long id;
    @NotEmpty
    private Boolean acquired;
    @NotEmpty
    private Boolean suspended;
    @NotEmpty
    private ZonedDateTime createDateTime;
    @NotEmpty
    private HabitDto habit;
    @NotEmpty
    private HabitStatusDto habitStatus;
    @NotEmpty
    private List<HabitStatisticDto> habitStatistic;
}
