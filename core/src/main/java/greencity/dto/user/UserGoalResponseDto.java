package greencity.dto.user;

import greencity.entity.enums.GoalStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserGoalResponseDto {
    private Long id;
    private String text;
    private GoalStatus status;
}
