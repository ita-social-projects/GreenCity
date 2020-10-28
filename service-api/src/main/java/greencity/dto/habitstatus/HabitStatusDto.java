package greencity.dto.habitstatus;

import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import java.time.LocalDateTime;
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
public class HabitStatusDto {
    @NotNull
    @Min(1)
    private Long id;
    @NotEmpty
    private Integer workingDays;
    @NotEmpty
    private Integer habitStreak;
    @NotEmpty
    private LocalDateTime lastEnrollmentDate;
    @NotNull
    @Min(1)
    private Long habitAssignId;
    @NotEmpty
    private List<HabitStatusCalendarDto> habitStatusCalendarDtos;
}
