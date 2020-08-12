package greencity.dto.habit;

import greencity.entity.HabitDictionary;
import greencity.entity.HabitStatistic;
import greencity.entity.HabitStatus;
import greencity.entity.User;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AddHabitDto {
    @NotEmpty
    private HabitDictionary habitDictionary;

    @NotEmpty
    private List<User> users;

    @NotEmpty
    private Boolean statusHabit;

    @NotEmpty
    private ZonedDateTime createDate;

    @NotEmpty
    private List<HabitStatistic> habitStatistics;

    @NotEmpty
    private List<HabitStatus> habitStatuses;
}