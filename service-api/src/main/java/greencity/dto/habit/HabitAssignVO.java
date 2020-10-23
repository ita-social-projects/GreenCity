package greencity.dto.habit;

import greencity.dto.user.UserVO;
import java.time.ZonedDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignVO {
    private Long id;
    private Boolean acquired;
    private Boolean suspended;
    private ZonedDateTime createDateTime;
    private HabitVO habitVO;
    private UserVO userVO;
}
