package greencity.dto.user;

import greencity.entity.enums.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGoalResponseDto {
    private Long id;
    private String text;
    private GoalStatus status;
}
