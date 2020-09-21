package greencity.service.impl;


import greencity.ModelUtils;
import greencity.entity.Habit;
import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import greencity.entity.User;
import greencity.repository.HabitStatusCalendarRepo;
import greencity.service.HabitStatusCalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class HabitStatusCalendarServiceImplTest {

    HabitStatusCalendarService habitStatusCalendarService;
    @Mock
    private HabitStatusCalendarRepo habitStatusCalendarRepo;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        habitStatusCalendarService = new HabitStatusCalendarServiceImpl(habitStatusCalendarRepo);
    }

    @Test
    void save() {
        HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar();
        habitStatusCalendarService.save(habitStatusCalendar);
        verify(habitStatusCalendarRepo).save(habitStatusCalendar);
    }

    @Test
    void findHabitStatusCalendarByEnrollDateAndHabitStatus() {
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

        when(habitStatusCalendarRepo.findHabitStatusCalendarByEnrollDateAndHabitStatus(date, habitStatus)).thenReturn(habitStatusCalendar);
        assertEquals(habitStatusCalendar, habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(date, habitStatus));
    }

    @Test
    void delete() {
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

        habitStatusCalendarService.delete(habitStatusCalendar);
        verify(habitStatusCalendarRepo).delete(habitStatusCalendar);
    }

    @Test
    void findTopByEnrollDateAndHabitStatus() {
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

        when(habitStatusCalendarRepo.findTopByEnrollDateAndHabitStatus(habitStatus)).thenReturn(date);
        assertEquals(date ,habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatus));
    }

    @Test
    void findEnrolledDatesAfter() {
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
        List<HabitStatusCalendar> calendarList = Collections.singletonList(habitStatusCalendar);
        List<LocalDate> localDates = Collections.singletonList(date);

        when(habitStatusCalendarRepo.findAllByEnrollDateAfterAndHabitStatus(date, habitStatus)).thenReturn(calendarList);
        assertEquals(localDates, habitStatusCalendarService.findEnrolledDatesAfter(date,habitStatus));
    }

    @Test
    void findEnrolledDatesBefore() {
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
        List<HabitStatusCalendar> calendarList = Collections.singletonList(habitStatusCalendar);
        List<LocalDate> localDates = Collections.singletonList(date);
        when(habitStatusCalendarRepo.findAllByEnrollDateBeforeAndHabitStatus(date, habitStatus)).thenReturn(calendarList);
        assertEquals(localDates, habitStatusCalendarService.findEnrolledDatesBefore(date,habitStatus));
    }

}
