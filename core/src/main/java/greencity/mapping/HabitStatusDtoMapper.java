package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.entity.HabitStatus;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitStatus} into
 * {@link HabitStatusDto}.
 */
@Component
public class HabitStatusDtoMapper extends AbstractConverter<HabitStatus, HabitStatusDto> {
    /**
     * Method convert {@link HabitStatus} to {@link HabitStatusDto}.
     *
     * @return {@link HabitStatusDto}
     */
    @Override
    protected HabitStatusDto convert(HabitStatus habitStatus) {
        return HabitStatusDto.builder()
            .id(habitStatus.getId())
            .habitStreak(habitStatus.getHabitStreak())
            .lastEnrollmentDate(habitStatus.getLastEnrollmentDate())
            .workingDays(habitStatus.getWorkingDays())
            .habitAssign(
                HabitAssignDto.builder()
                    .id(habitStatus.getHabitAssign().getId())
                    .acquired(habitStatus.getHabitAssign().getAcquired())
                    .createDateTime(habitStatus.getHabitAssign().getCreateDate())
                    .suspended(habitStatus.getHabitAssign().getSuspended())
                    .habitId(habitStatus.getHabitAssign().getHabit().getId()).build())
            .build();
    }
}
