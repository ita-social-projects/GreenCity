
package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.entity.HabitStatistic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class AddHabitStatisticDtoMapperTest {
    @InjectMocks
    private AddHabitStatisticDtoMapper addHabitStatisticDtoMapper;

    @Test
    void convertTest() {
        HabitStatistic habitStatistic = ModelUtils.getHabitStatistic();

        AddHabitStatisticDto expected = AddHabitStatisticDto.builder()
            .amountOfItems(habitStatistic.getAmountOfItems())
            .createDate(habitStatistic.getCreateDate())
            .habitRate(habitStatistic.getHabitRate())
            .build();

        assertEquals(expected, addHabitStatisticDtoMapper.convert(habitStatistic));
    }
}
