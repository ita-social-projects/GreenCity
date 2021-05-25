package greencity.mapping;

import greencity.dto.habit.HabitAssignVO;
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
            .habitAssignVO(HabitAssignVO.builder()
                .id(habitStatusCalendar.getHabitAssign().getId())
                .build())
            .build();
    }
}
