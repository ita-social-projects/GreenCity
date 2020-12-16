package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.dto.goal.GoalRequestDto;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.enums.GoalStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoalRequestDtoMapperTest {
    @InjectMocks
    GoalRequestDtoMapper goalRequestDtoMapper;

    @Test
    void convert() {
        GoalRequestDto goalRequestDto = new GoalRequestDto();
        goalRequestDto.setId(1L);

        UserGoal expected = UserGoal.builder()
            .goal(Goal.builder().id(goalRequestDto.getId()).build())
            .status(GoalStatus.ACTIVE)
            .build();

        UserGoal actual = goalRequestDtoMapper.convert(goalRequestDto);
        assertEquals(expected.getGoal().getId(), actual.getGoal().getId());
    }
}
