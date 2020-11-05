package greencity.mapping;

import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitStatusCalendarVO} into
 * {@link HabitStatusCalendar}.
 */
@Component
public class HabitStatusCalendarMapper extends AbstractConverter<HabitStatusCalendarVO, HabitStatusCalendar> {
    /**
     * Method convert {@link HabitStatusCalendarVO} to {@link HabitStatusCalendar}.
     *
     * @return {@link HabitStatusCalendar}
     */
    @Override
    protected HabitStatusCalendar convert(HabitStatusCalendarVO habitStatusCalendarVO) {
        return HabitStatusCalendar.builder()
            .id(habitStatusCalendarVO.getId())
            .enrollDate(habitStatusCalendarVO.getEnrollDate())
            .habitStatus(HabitStatus.builder()
                .id(habitStatusCalendarVO.getHabitStatusVO().getId())
                .habitStreak(habitStatusCalendarVO.getHabitStatusVO().getHabitStreak())
                .lastEnrollmentDate(habitStatusCalendarVO.getHabitStatusVO().getLastEnrollmentDate())
                .workingDays(habitStatusCalendarVO.getHabitStatusVO().getWorkingDays()).build())
            .build();
    }
}
