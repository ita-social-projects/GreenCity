package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
class HabitStatisticMapperTest {
    @InjectMocks
    private HabitStatisticMapper habitStatisticMapper;

    @Test
    void convertTest() {
        AddHabitStatisticDto addHabitStatisticDto = ModelUtils.addHabitStatisticDto();

        HabitAssign habitAssign = HabitAssign.builder().id(addHabitStatisticDto.getHabitAssignId()).build();
        HabitStatistic expected = new HabitStatistic(addHabitStatisticDto.getHabitAssignId(),
            addHabitStatisticDto.getHabitRate(),
            addHabitStatisticDto.getCreateDate(),
            addHabitStatisticDto.getAmountOfItems(),
                habitAssign);
        assertNotEquals(expected, habitStatisticMapper.convert(addHabitStatisticDto));
    }
}

