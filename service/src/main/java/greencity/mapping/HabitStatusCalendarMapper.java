package greencity.mapping;

import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitStatusCalendarVO}
 * into {@link HabitStatusCalendar}.
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
            .habitAssign(HabitAssign.builder()
                .id(habitStatusCalendarVO.getHabitAssignVO().getId())
                .build())
            .build();
    }
}
