package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class HabitStatisticMapperTest {
    @InjectMocks
    private HabitStatisticMapper habitStatisticMapper;

    @Test
    void convertTest() {
        AddHabitStatisticDto addHabitStatisticDto = ModelUtils.addHabitStatisticDto();

        HabitStatistic expected = new HabitStatistic(addHabitStatisticDto.getId(),
            addHabitStatisticDto.getHabitRate(),
            addHabitStatisticDto.getCreatedOn(),
            addHabitStatisticDto.getAmountOfItems(),
            Habit.builder().id(addHabitStatisticDto.getHabitId()).build());

        assertEquals(expected, habitStatisticMapper.convert(addHabitStatisticDto));
    }
}
