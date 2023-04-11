package greencity.dto.habit;

import greencity.constant.AppConstant;
import greencity.enums.HabitAssignStatus;
import java.time.ZonedDateTime;
import javax.validation.constraints.Max;
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
public class HabitAssignManagementDto {
    @NotNull
    private Long id;
    @NotNull
    private HabitAssignStatus status;
    @NotEmpty
    private ZonedDateTime createDateTime;
    @NotNull
    private Long habitId;
    @NotNull
    private Long userId;

    @Min(AppConstant.MIN_DAYS_DURATION)
    @Max(AppConstant.MAX_DAYS_DURATION)
    @NotNull
    private Integer duration;

    @NotNull
    private Integer workingDays;
    @NotNull
    private Integer habitStreak;
    @NotEmpty
    private ZonedDateTime lastEnrollment;
    private Boolean progressNotificationHasDisplayed;
}
