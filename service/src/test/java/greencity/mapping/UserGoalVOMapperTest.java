package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.goal.GoalVO;
import greencity.dto.user.UserGoalVO;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.enums.GoalStatus;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserGoalVOMapperTest {
    @InjectMocks
    private UserGoalVOMapper mapper;

    @Test
    void convert() {
        UserGoal userGoal = ModelUtils.getPredefinedUserGoal();
        userGoal.setDateCompleted(LocalDateTime.now());
        userGoal.setId(1L);
        userGoal.setGoal(Goal.builder().id(13L).build());

        UserGoalVO expected = ModelUtils.getUserGoalVO();
        expected.setStatus(GoalStatus.ACTIVE);
        expected.setGoal(GoalVO.builder().id(13L).build());
        expected.setDateCompleted(userGoal.getDateCompleted());

        assertEquals(expected, mapper.convert(userGoal));
    }
}