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
class HabitStatusCalendarVOMapperTest {

    @InjectMocks
    HabitStatusCalendarVOMapper habitStatusCalendarVOMapper;

    @Test
    void convert() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitStatusCalendar habitStatusCalendar = ModelUtils.getHabitStatusCalendar();
        habitStatusCalendar.setHabitAssign(habitAssign);
        HabitStatusCalendarVO habitStatusCalendarVO = HabitStatusCalendarVO.builder()
            .id(habitStatusCalendar.getId())
            .enrollDate(habitStatusCalendar.getEnrollDate())
            .habitAssignVO(HabitAssignVO.builder()
                .id(habitStatusCalendar.getHabitAssign().getId())
                .build())
            .build();
        HabitStatusCalendarVO expected = habitStatusCalendarVOMapper.convert(habitStatusCalendar);
        assertEquals(expected, habitStatusCalendarVO);
    }
}