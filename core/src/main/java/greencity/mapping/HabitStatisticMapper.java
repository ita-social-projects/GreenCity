package greencity.mapping;

import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.entity.HabitStatistic;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class HabitStatisticMapper extends AbstractConverter<AddHabitStatisticDto, HabitStatistic> {
    @Override
    protected HabitStatistic convert(AddHabitStatisticDto addHabitStatisticDto) {
        return HabitStatistic.builder()
            .amountOfItems(addHabitStatisticDto.getAmountOfItems())
            .createDate(addHabitStatisticDto.getCreateDate())
            .habitRate(addHabitStatisticDto.getHabitRate())
            .build();
    }
}
