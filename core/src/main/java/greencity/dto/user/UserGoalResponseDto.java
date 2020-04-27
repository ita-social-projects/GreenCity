package greencity.dto.user;

import greencity.entity.enums.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
