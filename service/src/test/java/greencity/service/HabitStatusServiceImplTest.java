package greencity.service;
/*
import greencity.ModelUtils;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.HabitStatusRepo;
import greencity.service.HabitStatusCalendarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class HabitStatusServiceImplTest {

    @Mock
    private HabitStatusRepo habitStatusRepo;
    @Mock
    private HabitStatusCalendarService habitStatusCalendarService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    HabitStatusServiceImpl habitStatusService;

    @Test
    void saveByHabit() {
        Habit habit = ModelUtils.getHabit();
        User user = ModelUtils.getUser();
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(0);
        habitStatus.setUser(user);

        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        when(habitStatusRepo.save(any())).thenReturn(habitStatus);
        habitStatusService.saveByHabit(habit, user);
        verify(habitStatusRepo).save(any());
    }

    @Test
    void deleteByUser() {
        Long userId = 1L;
        habitStatusService.deleteByUser(userId);
        verify(habitStatusRepo).deleteHabitStatusByUserId(userId);
    }

    @Test
    void findStatusByHabitIdAndUserId() {
        Long habitId = 1L;
        Long userId =1L;
        HabitStatusDto habitStatusDto = new HabitStatusDto();
        Habit habit = ModelUtils.getHabit();
        User user = ModelUtils.getUser();
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(0);
        habitStatus.setUser(user);
        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        when(habitStatusRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(habitStatus);
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(new HabitStatusDto());

        assertEquals(habitStatusDto, habitStatusService.findStatusByHabitIdAndUserId(habitId, userId));
    }

    @Test
    void enrollHabit() {
        Long habitId = 1L;
        Long userId =1L;
        Habit habit = ModelUtils.getHabit();
        User user = ModelUtils.getUser();
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(0);
        habitStatus.setUser(user);
        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        HabitStatusDto habitStatusDto =new HabitStatusDto();

        when(habitStatusRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatus)).thenReturn(LocalDate.of(2020, 9, 17));
        when(modelMapper.map(habitStatusRepo.save(habitStatus), HabitStatusDto.class)).thenReturn(new HabitStatusDto());

        assertEquals(habitStatusDto, habitStatusService.enrollHabit(habitId,userId));
    }

    @Test
    void enrollHabit2() {
        Long habitId = 1L;
        Long userId =1L;
        Habit habit = ModelUtils.getHabit();
        User user = ModelUtils.getUser();
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(0);
        habitStatus.setUser(user);
        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        HabitStatusDto habitStatusDto =new HabitStatusDto();

        when(habitStatusRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatus)).thenReturn(null);
        when(modelMapper.map(habitStatusRepo.save(habitStatus), HabitStatusDto.class)).thenReturn(new HabitStatusDto());

        assertEquals(habitStatusDto, habitStatusService.enrollHabit(habitId,userId));
    }


    @Test
    void enrollHabitBadRequest() {
        Long habitId = 1L;
        Long userId =1L;
        Habit habit = ModelUtils.getHabit();
        User user = ModelUtils.getUser();
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(0);
        habitStatus.setUser(user);
        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());

        when(habitStatusRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatus)).thenReturn((LocalDate.of(2060, 9, 19)));

        assertThrows(BadRequestException.class, () ->
                habitStatusService.enrollHabit(habitId,userId)
        );

    }


    @Test
    void unenrollHabit() {
        Long habitId = 1L;
        Long userId =1L;
        Habit habit = ModelUtils.getHabit();
        User user = ModelUtils.getUser();
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(0);
        habitStatus.setUser(user);
        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        LocalDate date = LocalDate.now();
        HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar(date, habitStatus);

        when(habitStatusRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(habitStatus);
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);
        when(habitStatusCalendarService
                .findHabitStatusCalendarByEnrollDateAndHabitStatus(date, habitStatus)).thenReturn(habitStatusCalendar);
        habitStatusService.unenrollHabit(date,habitId,userId);
        verify(habitStatusCalendarService, times(1)).delete(habitStatusCalendar);
    }

    @Test
    void unenrollHabitBadRequest() {
        Long habitId = 1L;
        Long userId =1L;
        Habit habit = ModelUtils.getHabit();
        User user = ModelUtils.getUser();
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(2);
        habitStatus.setUser(user);
        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        LocalDate date = LocalDate.now();

        when(habitStatusRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(habitStatus);
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);
        when(habitStatusCalendarService
                .findHabitStatusCalendarByEnrollDateAndHabitStatus(date, habitStatus)).thenReturn(null);

        assertThrows(BadRequestException.class, () ->
                habitStatusService.unenrollHabit(date, habitId,userId)
        );

    }


    @Test
    void enrollHabitInDate() {
        Long habitId = 1L;
        Long userId =1L;
        Habit habit = ModelUtils.getHabit();
        User user = ModelUtils.getUser();
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(2);
        habitStatus.setUser(user);
        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate yesterday2 = today.minusDays(2);
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(yesterday2);
        dateList.add(yesterday);

        when(habitStatusRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findEnrolledDatesBefore(yesterday, habitStatus)).thenReturn(dateList);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(yesterday, habitStatus)).thenReturn(null);
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);

        habitStatusService.enrollHabitInDate(habitId,userId, yesterday);
        verify(habitStatusRepo, times(1)).save(habitStatus);

    }

    @Test
    void enrollHabitInDate2() {
        Long habitId = 1L;
        Long userId =1L;
        Habit habit = ModelUtils.getHabit();
        User user = ModelUtils.getUser();
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(2);
        habitStatus.setUser(user);
        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate yesterday2 = today.minusDays(2);
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(today);
        dateList.add(yesterday);
        List<LocalDate> dateList2 = Collections.singletonList(yesterday);

        when(habitStatusRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findEnrolledDatesAfter(yesterday2, habitStatus)).thenReturn(dateList);
        when(habitStatusCalendarService.findEnrolledDatesBefore(yesterday2, habitStatus)).thenReturn(dateList2);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(yesterday2, habitStatus)).thenReturn(null);
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);

        habitStatusService.enrollHabitInDate(habitId,userId, yesterday2);
        verify(habitStatusRepo, times(1)).save(habitStatus);
    }

    @Test
    void enrollHabitInDateBadRequest(){
        Long habitId = 1L;
        Long userId =1L;
        Habit habit = ModelUtils.getHabit();
        User user = ModelUtils.getUser();
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setId(1L);
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(2);
        habitStatus.setUser(user);
        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        LocalDate date = LocalDate.of(2020,9,17);
        HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar();
        habitStatusCalendar.setId(1L);
        LocalDate today = LocalDate.now();
        LocalDate yesterday2 = today.minusDays(2);
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(today);
        dateList.add(yesterday2);

        when(habitStatusRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findEnrolledDatesAfter(date, habitStatus)).thenReturn(dateList);
        when(habitStatusCalendarService.findEnrolledDatesBefore(date, habitStatus)).thenReturn(dateList);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(date, habitStatus)).thenReturn(habitStatusCalendar);

        assertThrows(BadRequestException.class, () ->
                habitStatusService.enrollHabitInDate(habitId, userId, date)
        );
    }

}
*/