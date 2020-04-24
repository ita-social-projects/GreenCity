package greencity.dto.user;

import greencity.entity.enums.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGoalResponseDto {
    private Long id;
    private String text;
    private GoalStatus status;
}
