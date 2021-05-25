package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class HabitStatusCalendarMapperTest {

    @InjectMocks
    HabitStatusCalendarMapper habitStatusCalendarMapper;

    @Test
    void convert() {
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();
        HabitStatusCalendarVO habitStatusCalendarVO = ModelUtils.getHabitStatusCalendarVO();
        habitStatusCalendarVO.setHabitAssignVO(habitAssignVO);
        HabitStatusCalendar habitStatusCalendar = HabitStatusCalendar.builder()
            .id(habitStatusCalendarVO.getId())
            .enrollDate(habitStatusCalendarVO.getEnrollDate())
            .habitAssign(HabitAssign.builder().id(habitStatusCalendarVO.getHabitAssignVO().getId()).build())
            .build();
        HabitStatusCalendar expected = habitStatusCalendarMapper.convert(habitStatusCalendarVO);
        assertEquals(expected, habitStatusCalendar);
    }
}