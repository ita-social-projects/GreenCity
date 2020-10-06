/*
package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.dto.habitstatistic.HabitDictionaryDto;
import greencity.entity.Habit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class HabitCreateDtoMapperTest {
    @InjectMocks
    private HabitCreateDtoMapper habitCreateDtoMapper;

    @Test
    public void convertTest() {
        Habit habit = ModelUtils.getHabit();

        HabitCreateDto expected = new HabitCreateDto();
        HabitDictionaryDto habitDictionaryDto = new HabitDictionaryDto();
        habitDictionaryDto.setImage(habit.getHabitDictionary().getImage());
        habitDictionaryDto.setId(habit.getHabitDictionary().getId());
        expected.setId(habit.getId());
        expected.setStatus(habit.getStatusHabit());
        expected.setHabitDictionary(habitDictionaryDto);

        assertEquals(expected, habitCreateDtoMapper.convert(habit));
    }
}
*/
