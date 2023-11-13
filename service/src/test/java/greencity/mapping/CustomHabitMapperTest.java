package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.CustomHabitDtoRequest;
import greencity.entity.Habit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CustomHabitMapperTest {
    @InjectMocks
    private CustomHabitMapper customHabitMapper;

    @Test
    void convertTest() {
        CustomHabitDtoRequest addCustomHabitDtoRequest = ModelUtils.getAddCustomHabitDtoRequest();

        Habit expected = Habit.builder()
            .complexity(addCustomHabitDtoRequest.getComplexity())
            .defaultDuration(addCustomHabitDtoRequest.getDefaultDuration())
            .isCustomHabit(true)
            .build();
        assertEquals(expected, customHabitMapper.convert(addCustomHabitDtoRequest));
    }
}
