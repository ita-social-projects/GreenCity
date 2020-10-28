package greencity.mapping;

import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.entity.HabitStatus;
import java.util.stream.Collectors;
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
            .habitAssignId(habitStatus.getHabitAssign().getId())
            .habitStatusCalendarDtos(habitStatus.getHabitStatusCalendars().stream()
                .map(habitStatusCalendar -> HabitStatusCalendarDto.builder()
                    .id(habitStatusCalendar.getId())
                    .enrollDate(habitStatusCalendar.getEnrollDate())
                    .build()).collect(Collectors.toList()))
            .build();
    }
}
