package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.entity.HabitAssign;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitAssign} into
 * {@link HabitAssignDto}.
 */
@Component
public class HabitAssignDtoMapper extends AbstractConverter<HabitAssign, HabitAssignDto> {
    /**
     * Method convert {@link HabitAssign} to {@link HabitAssignDto}.
     *
     * @return {@link HabitAssignDto}
     */
    @Override
    protected HabitAssignDto convert(HabitAssign habitAssign) {
        return HabitAssignDto.builder()
            .id(habitAssign.getId())
            .acquired(habitAssign.getAcquired())
            .suspended(habitAssign.getSuspended())
            .createDateTime(habitAssign.getCreateDate())
            .habitId(habitAssign.getHabit().getId())
            .userId(habitAssign.getUser().getId())
            .duration(habitAssign.getDuration())
            .build();
    }
}
