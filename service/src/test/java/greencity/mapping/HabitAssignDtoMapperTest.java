package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.entity.HabitAssign;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class HabitAssignDtoMapperTest {

    @InjectMocks
    private HabitAssignDtoMapper habitAssignDtoMapper;

    @Test
    void convertTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();

        HabitAssignDto expected = HabitAssignDto.builder()
            .id(habitAssign.getId())
            .acquired(habitAssign.getAcquired())
            .suspended(habitAssign.getSuspended())
            .createDateTime(habitAssign.getCreateDate())
            .userId(habitAssign.getUser().getId())
            .build();

        assertEquals(expected, habitAssignDtoMapper.convert(habitAssign));
    }
}
