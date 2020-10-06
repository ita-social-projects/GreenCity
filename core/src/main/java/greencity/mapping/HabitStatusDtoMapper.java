package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
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
            .lastEnrollmentDate(habitStatus.getLastEnrollmentDate())
            .workingDays(habitStatus.getWorkingDays())
            .habitAssign(
                HabitAssignDto.builder()
                    .id(habitStatus.getHabitAssign().getId())
                    .acquired(habitStatus.getHabitAssign().getAcquired())
                    .createDateTime(habitStatus.getHabitAssign().getCreateDate())
                    .suspended(habitStatus.getHabitAssign().getSuspended())
                    .habit(HabitDto.builder()
                        .id(habitStatus.getHabitAssign().getHabit().getId())
                        .build())
                    .build())
            .build();
    }
}
