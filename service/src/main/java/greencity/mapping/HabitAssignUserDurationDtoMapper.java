package greencity.mapping;

import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.entity.HabitAssign;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitAssign} into
 * {@link greencity.dto.habit.HabitAssignUserDurationDto}.
 */
@Component
public class HabitAssignUserDurationDtoMapper extends
    AbstractConverter<HabitAssign, HabitAssignUserDurationDto> {
    /**
     * Method convert {@link HabitAssign} to {@link HabitAssignUserDurationDto}.
     *
     * @return {@link HabitAssignUserDurationDto}
     */
    @Override
    protected HabitAssignUserDurationDto convert(HabitAssign habitAssign) {
        return HabitAssignUserDurationDto.builder()
            .habitAssignId(habitAssign.getId())
            .userId(habitAssign.getUser().getId())
            .habitId(habitAssign.getHabit().getId())
            .status(habitAssign.getStatus())
            .workingDays(habitAssign.getWorkingDays())
            .duration(habitAssign.getDuration())
            .build();
    }
}
