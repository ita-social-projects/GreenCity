package greencity.mapping;

import greencity.dto.user.UserGoalVO;
import greencity.entity.Goal;
import greencity.entity.HabitAssign;
import greencity.entity.UserGoal;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserGoalMapper extends AbstractConverter<UserGoalVO, UserGoal> {
    @Override
    protected UserGoal convert(UserGoalVO userGoalVO) {
        return UserGoal.builder()
            .id(userGoalVO.getId())
            .status(userGoalVO.getStatus())
            .habitAssign(HabitAssign.builder()
                .id(userGoalVO.getHabitAssign().getId())
                .status(userGoalVO.getHabitAssign().getStatus())
                .habitStreak(userGoalVO.getHabitAssign().getHabitStreak())
                .duration(userGoalVO.getHabitAssign().getDuration())
                .lastEnrollmentDate(userGoalVO.getHabitAssign().getLastEnrollmentDate())
                .workingDays(userGoalVO.getHabitAssign().getWorkingDays())
                .build())
            .goal(Goal.builder()
                .id(userGoalVO.getGoal().getId())
                .build())
            .dateCompleted(userGoalVO.getDateCompleted())
            .build();
    }
}
