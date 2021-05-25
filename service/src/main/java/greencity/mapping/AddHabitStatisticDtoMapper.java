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
            .amountOfItems(habitStatistic.getAmountOfItems())
            .createDate(habitStatistic.getCreateDate())
            .habitRate(habitStatistic.getHabitRate())
            .build();
    }
}
