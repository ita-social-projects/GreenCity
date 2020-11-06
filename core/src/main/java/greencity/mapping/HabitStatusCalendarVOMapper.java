package greencity.mapping;

import greencity.dto.habitstatus.HabitStatusVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitStatusCalendar;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitStatusCalendar}
 * into {@link HabitStatusCalendarVO}.
 */
@Component
public class HabitStatusCalendarVOMapper extends AbstractConverter<HabitStatusCalendar, HabitStatusCalendarVO> {
    /**
     * Method convert {@link HabitStatusCalendar} to {@link HabitStatusCalendarVO}.
     *
     * @return {@link HabitStatusCalendarVO}
     */
    @Override
    protected HabitStatusCalendarVO convert(HabitStatusCalendar habitStatusCalendar) {
        return HabitStatusCalendarVO.builder()
            .id(habitStatusCalendar.getId())
            .enrollDate(habitStatusCalendar.getEnrollDate())
            .habitStatusVO(HabitStatusVO.builder()
                .id(habitStatusCalendar.getHabitStatus().getId())
                .habitStreak(habitStatusCalendar.getHabitStatus().getHabitStreak())
                .lastEnrollmentDate(habitStatusCalendar.getHabitStatus().getLastEnrollmentDate())
                .workingDays(habitStatusCalendar.getHabitStatus().getWorkingDays()).build())
            .build();
    }
}
