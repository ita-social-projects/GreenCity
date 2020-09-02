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
            .id(addHabitStatisticDto.getId())
            .amountOfItems(addHabitStatisticDto.getAmountOfItems())
            .createdOn(addHabitStatisticDto.getCreatedOn())
            .habitRate(addHabitStatisticDto.getHabitRate())
            .build();
    }
}
