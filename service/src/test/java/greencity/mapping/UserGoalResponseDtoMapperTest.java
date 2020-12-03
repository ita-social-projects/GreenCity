package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.UserGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class UserGoalResponseDtoMapperTest {
    @InjectMocks
    private UserGoalResponseDtoMapper userGoalResponseDtoMapper;

    @Test
    void convertTest() {
        UserGoal userGoal = ModelUtils.getCustomUserGoal();

        UserGoalResponseDto expected = UserGoalResponseDto.builder()
            .id(userGoal.getId())
            .status(userGoal.getStatus())
            .build();

        assertEquals(expected, userGoalResponseDtoMapper.convert(userGoal));
    }
}