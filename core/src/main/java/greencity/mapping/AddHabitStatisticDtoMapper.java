package greencity.mapping;

import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.entity.HabitStatistic;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddHabitStatisticDtoMapper extends AbstractConverter<HabitStatistic, AddHabitStatisticDto> {
    @Override
    protected AddHabitStatisticDto convert(HabitStatistic habitStatistic) {
        return AddHabitStatisticDto.builder()
            .id(habitStatistic.getId())
            .amountOfItems(habitStatistic.getAmountOfItems())
            .createdOn(habitStatistic.getCreatedOn())
            .habitRate(habitStatistic.getHabitRate())
            .habitId(habitStatistic.getHabit().getId())
            .build();
    }
}
