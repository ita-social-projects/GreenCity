package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.entity.*;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class HabitStatisticMapperTest {
    @InjectMocks
    private HabitStatisticMapper habitStatisticMapper;

    @Test
    public void convertTest() {
        AddHabitStatisticDto addHabitStatisticDto = ModelUtils.addHabitStatisticDto();

        HabitStatistic expected = new HabitStatistic(addHabitStatisticDto.getId(),
            addHabitStatisticDto.getHabitRate(),
            addHabitStatisticDto.getCreatedOn(),
            addHabitStatisticDto.getAmountOfItems(),
            Habit.builder().id(addHabitStatisticDto.getHabitId()).build());

        assertEquals(expected, habitStatisticMapper.convert(addHabitStatisticDto));
    }
}
