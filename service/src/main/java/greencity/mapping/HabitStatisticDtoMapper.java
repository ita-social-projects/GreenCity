package greencity.mapping;

import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.HabitStatistic;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitStatistic} into
 * {@link HabitStatisticDto}.
 */
@Component
public class HabitStatisticDtoMapper extends AbstractConverter<HabitStatistic, HabitStatisticDto> {
    /**
     * Method convert {@link HabitStatistic} to {@link HabitStatisticDto}.
     *
     * @return {@link HabitStatisticDto}
     */
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
