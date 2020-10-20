package greencity.dto.goal;

import greencity.dto.user.UserGoalVO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = {"userGoals"})
@Builder
public class CustomGoalVO {
    private Long id;

    private String text;

    private List<UserGoalVO> userGoals = new ArrayList<>();
}
