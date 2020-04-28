package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.entity.HabitStatistic;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AddHabitStatisticDtoMapperTest {
    @InjectMocks
    private AddHabitStatisticDtoMapper addHabitStatisticDtoMapper;

    @Test
    public void convertTest() {
        HabitStatistic habitStatistic = ModelUtils.getHabitStatistic();

        AddHabitStatisticDto expected = AddHabitStatisticDto.builder()
            .id(habitStatistic.getId())
            .amountOfItems(habitStatistic.getAmountOfItems())
            .createdOn(habitStatistic.getCreatedOn())
            .habitRate(habitStatistic.getHabitRate())
            .habitId(habitStatistic.getHabit().getId())
            .build();

        assertEquals(expected, addHabitStatisticDtoMapper.convert(habitStatistic));
    }
}
