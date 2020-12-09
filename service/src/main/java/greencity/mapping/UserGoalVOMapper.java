package greencity.mapping;

import greencity.dto.goal.GoalVO;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.user.UserGoalVO;
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
            .habitAssign(HabitAssignVO.builder()
                .id(userGoal.getHabitAssign().getId()).build())
            .dateCompleted(userGoal.getDateCompleted())
            .build();
    }
}
