package greencity.mapping;

import greencity.dto.goal.GoalVO;
import greencity.dto.user.UserGoalVO;
import greencity.dto.user.UserVO;
import greencity.entity.UserGoal;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserGoalVOMapper extends AbstractConverter<UserGoal, UserGoalVO> {
    @Override
    protected UserGoalVO convert(UserGoal userGoal) {
        return UserGoalVO.builder()
            .id(userGoal.getId())
            .goal(GoalVO.builder()
                .id(userGoal.getGoal().getId())
                .build())
            .status(userGoal.getStatus())
            .user(UserVO.builder()
                .id(userGoal.getUser().getId()).build())
            .dateCompleted(userGoal.getDateCompleted())
            .build();
    }
}
