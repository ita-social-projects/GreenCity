package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.entity.HabitAssign;
import java.util.stream.Collectors;
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
            .status(habitAssign.getStatus())
            .createDateTime(habitAssign.getCreateDate())
            .userId(habitAssign.getUser().getId())
            .duration(habitAssign.getDuration())
            .habitStreak(habitAssign.getHabitStreak())
            .workingDays(habitAssign.getWorkingDays())
            .lastEnrollmentDate(habitAssign.getLastEnrollmentDate())
            .habitStatusCalendarDtoList(habitAssign.getHabitStatusCalendars().stream().map(
                habitStatusCalendar -> HabitStatusCalendarDto.builder()
                    .id(habitStatusCalendar.getId())
                    .enrollDate(habitStatusCalendar.getEnrollDate())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
