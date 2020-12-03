package greencity.dto.habit;

import greencity.dto.user.UserVO;
import java.time.ZonedDateTime;
import greencity.enums.HabitAssignStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HabitAssignVO {
    private Long id;
    private HabitAssignStatus status;
    private ZonedDateTime createDateTime;
    private HabitVO habitVO;
    private UserVO userVO;
    private Integer duration;
    private Integer habitStreak;
    private Integer workingDays;
    private ZonedDateTime lastEnrollmentDate;
}
