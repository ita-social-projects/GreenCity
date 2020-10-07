/*
package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.entity.HabitStatistic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AddHabitStatisticDtoMapperTest {
    @InjectMocks
    private AddHabitStatisticDtoMapper addHabitStatisticDtoMapper;

    @Test
    void convertTest() {
        HabitStatistic habitStatistic = ModelUtils.getHabitStatistic();

        AddHabitStatisticDto expected = AddHabitStatisticDto.builder()
            .id(habitStatistic.getId())
            .amountOfItems(habitStatistic.getAmountOfItems())
            .createdOn(habitStatistic.getCreatedOn())
            .habitRate(habitStatistic.getHabitRate())
            .habitAssignId(habitStatistic.getHabitAssign().getId())
            .build();

        assertEquals(expected, addHabitStatisticDtoMapper.convert(habitStatistic));
    }
}
*/
