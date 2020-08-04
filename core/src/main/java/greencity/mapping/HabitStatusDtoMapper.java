package greencity.mapping;

import greencity.dto.habitstatistic.HabitDto;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.entity.HabitStatus;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class HabitStatusDtoMapper extends AbstractConverter<HabitStatus, HabitStatusDto> {

    @Override
    protected HabitStatusDto convert(HabitStatus habitStatus) {
        return HabitStatusDto.builder()
            .id(habitStatus.getId())
            .habitStreak(habitStatus.getHabitStreak())
            .habit(HabitDto.builder()
                .id(habitStatus.getHabit().getId())
                .status(habitStatus.getHabit().getStatusHabit())
                .createDate(habitStatus.getHabit().getCreateDate())
                .build())
            .lastEnrollmentDate(habitStatus.getLastEnrollmentDate())
            .suspended(habitStatus.isSuspended())
            .workingDays(habitStatus.getWorkingDays())
            .build();
    }
}
