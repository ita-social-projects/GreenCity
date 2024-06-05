package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.MutualHabitAssignDto;
import greencity.entity.HabitAssign;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitAssign} into
 * {@link MutualHabitAssignDto}.
 */
@Component
public class MutualHabitAssignDtoMapper extends AbstractConverter<HabitAssign, MutualHabitAssignDto> {
    /**
     * Method convert {@link HabitAssign} to {@link MutualHabitAssignDto}.
     *
     * @return {@link MutualHabitAssignDto}
     */
    @Override
    protected MutualHabitAssignDto convert(HabitAssign habitAssign) {
        return MutualHabitAssignDto.builder()
            .id(habitAssign.getId())
            .status(habitAssign.getStatus())
            .userId(habitAssign.getUser().getId())
            .duration(habitAssign.getDuration())
            .workingDays(habitAssign.getWorkingDays())
            .build();
    }
}
