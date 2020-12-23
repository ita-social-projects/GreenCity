package greencity.service;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import greencity.repository.HabitStatusCalendarRepo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class HabitStatusCalendarServiceImplTest {
    @Mock
    private HabitStatusCalendarRepo habitStatusCalendarRepo;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HabitStatusCalendarServiceImpl habitStatusCalendarService;

    @Test
    void findHabitStatusCalendarByEnrollDateAndHabitAssign() {
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitStatusCalendarVO habitStatusCalendarVO = ModelUtils.getHabitStatusCalendarVO();
        HabitStatusCalendar habitStatusCalendar = ModelUtils.getHabitStatusCalendar();
        when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        when(habitStatusCalendarRepo.findHabitStatusCalendarByEnrollDateAndHabitAssign(LocalDate.now(), habitAssign))
            .thenReturn(habitStatusCalendar);
        when(modelMapper.map(habitStatusCalendar, HabitStatusCalendarVO.class)).thenReturn(habitStatusCalendarVO);
        assertEquals(habitStatusCalendarVO, habitStatusCalendarService
            .findHabitStatusCalendarByEnrollDateAndHabitAssign(LocalDate.now(), habitAssignVO));
        assertEquals(null, habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitAssign(LocalDate.now(),
            new HabitAssignVO()));
    }

    @Test
    void save() {
        HabitStatusCalendarVO habitStatusCalendarVO = ModelUtils.getHabitStatusCalendarVO();
        HabitStatusCalendar habitStatusCalendar = ModelUtils.getHabitStatusCalendar();
        when(modelMapper.map(habitStatusCalendarVO, HabitStatusCalendar.class))
            .thenReturn(habitStatusCalendar);
        when(modelMapper.map(habitStatusCalendarRepo.save(habitStatusCalendar), HabitStatusCalendarVO.class))
            .thenReturn(habitStatusCalendarVO);
        assertEquals(habitStatusCalendarVO, habitStatusCalendarService.save(habitStatusCalendarVO));
    }

    @Test
    void delete() {
        HabitStatusCalendar habitStatusCalendar = ModelUtils.getHabitStatusCalendar();
        HabitStatusCalendarVO habitStatusCalendarVO = ModelUtils.getHabitStatusCalendarVO();
        when(modelMapper.map(habitStatusCalendarVO, HabitStatusCalendar.class))
            .thenReturn(habitStatusCalendar);
        doNothing().when(habitStatusCalendarRepo).delete(habitStatusCalendar);
        habitStatusCalendarService.delete(habitStatusCalendarVO);
        verify(habitStatusCalendarRepo, times(1)).delete(habitStatusCalendar);
    }

    @Test
    void findTopByEnrollDateAndHabitAssign() {
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        when(habitStatusCalendarRepo.findTopByEnrollDateAndHabitAssign(habitAssign))
            .thenReturn(LocalDate.now());
        assertEquals(LocalDate.now(), habitStatusCalendarService.findTopByEnrollDateAndHabitAssign(habitAssignVO));
    }

    @Test
    void findEnrolledDatesAfter() {
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitStatusCalendar habitStatusCalendar = ModelUtils.getHabitStatusCalendar();
        List<HabitStatusCalendar> list = Collections.singletonList(habitStatusCalendar);
        List<LocalDate> dates = new ArrayList<>();
        when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        when(habitStatusCalendarRepo.findAllByEnrollDateAfterAndHabitAssign(LocalDate.now(), habitAssign))
            .thenReturn(list);
        for (HabitStatusCalendar calendar : list) {
            dates.add(calendar.getEnrollDate());
        }
        assertEquals(dates, habitStatusCalendarService.findEnrolledDatesAfter(LocalDate.now(), habitAssignVO));
    }

    @Test
    void findEnrolledDatesBefore() {
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitStatusCalendar habitStatusCalendar = ModelUtils.getHabitStatusCalendar();
        List<HabitStatusCalendar> list = Collections.singletonList(habitStatusCalendar);
        List<LocalDate> dates = new ArrayList<>();
        dates.add(habitStatusCalendar.getEnrollDate());
        when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        when(habitStatusCalendarRepo.findAllByEnrollDateBeforeAndHabitAssign(LocalDate.now(), habitAssign))
            .thenReturn(list);
        assertEquals(dates, habitStatusCalendarService.findEnrolledDatesBefore(LocalDate.now(), habitAssignVO));
    }

    @Test
    void deleteAllByHabitAssign() {
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        doNothing().when(habitStatusCalendarRepo).deleteAllByHabitAssign(habitAssign);
        habitStatusCalendarService.deleteAllByHabitAssign(habitAssignVO);
        verify(habitStatusCalendarRepo, times(1)).deleteAllByHabitAssign(habitAssign);

    }
}