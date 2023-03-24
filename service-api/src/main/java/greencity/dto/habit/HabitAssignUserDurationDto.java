package greencity.dto.habit;

import greencity.enums.HabitAssignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignUserDurationDto {
    private Long habitAssignId;
    private Long userId;
    private Long habitId;
    private HabitAssignStatus status;
    private Integer workingDays;
    private Integer duration;
}
