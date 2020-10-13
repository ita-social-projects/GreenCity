package greencity.mapping;

import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.HabitStatistic;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class HabitStatisticDtoMapper extends AbstractConverter<HabitStatistic, HabitStatisticDto> {
    @Override
    protected HabitStatisticDto convert(HabitStatistic habitStatistic) {
        return HabitStatisticDto.builder()
            .id(habitStatistic.getId())
            .amountOfItems(habitStatistic.getAmountOfItems())
            .createDate(habitStatistic.getCreateDate())
            .habitRate(habitStatistic.getHabitRate())
            .habitAssignId(habitStatistic.getHabitAssign().getId())
            .build();
    }
}
