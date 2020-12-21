package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.goal.GoalVO;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.user.UserGoalVO;
import greencity.entity.UserGoal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserGoalMapperTest {
    @InjectMocks
    private UserGoalMapper mapper;

    @Test
    void convert() {
        UserGoal expected = ModelUtils.getUserGoal();
        UserGoalVO toConvert = UserGoalVO.builder()
            .id(expected.getId())
            .status(expected.getStatus())
            .habitAssign(HabitAssignVO.builder()
                .id(expected.getHabitAssign().getId())
                .status(expected.getHabitAssign().getStatus())
                .habitStreak(expected.getHabitAssign().getHabitStreak())
                .duration(expected.getHabitAssign().getDuration())
                .lastEnrollmentDate(expected.getHabitAssign().getLastEnrollmentDate())
                .workingDays(expected.getHabitAssign().getWorkingDays())
                .build())
            .goal(GoalVO.builder()
                .id(expected.getGoal().getId())
                .build())
            .dateCompleted(expected.getDateCompleted())
            .build();

        assertEquals(expected, mapper.convert(toConvert));
    }
}