package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatus;
import greencity.entity.HabitStatusCalendar;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.HabitStatusRepo;
import greencity.service.HabitStatusCalendarService;
import greencity.service.HabitStatusService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HabitStatusServiceImpl implements HabitStatusService {
    private HabitStatusRepo habitStatusRepo;
    private HabitStatusCalendarService habitStatusCalendarService;
    private ModelMapper modelMapper;

    /**
     * Method save {@link HabitStatusCalendar} for user
     * @param habit target habit
     * @param user target habit
     */
    @Override
    public void saveByHabit(Habit habit, User user) {
        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setHabit(habit);
        habitStatus.setWorkingDays(0);
        habitStatus.setUser(user);
        habitStatus.setCreateDate(LocalDateTime.now());
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        habitStatusRepo.save(habitStatus);
        HabitStatusCalendar habitCalendar = new HabitStatusCalendar(LocalDateTime.now(), habitStatus);
        habitStatusCalendarService.save(habitCalendar);
    }

    /**
     * Method delete {@link HabitStatus} by user
     * @param userId target userId
     */
    @Override
    public void deleteByUser(Long userId) {
        habitStatusRepo.deleteHabitStatusByUserId(userId);
    }

    /**
     * Find {@link HabitStatus} by habit and user id's
     * @param habitId target habit Id
     * @param userId target user Id
     * @return {@link HabitStatusDto}
     */
    @Override
    public HabitStatusDto findStatusByHabitIdAndUserId(Long habitId, Long userId) {
        return modelMapper.map(habitStatusRepo.findByHabitIdAndUserId(habitId, userId), HabitStatusDto.class);
    }

    /**
     * Method enroll {@link greencity.entity.Habit}
     * @param habitId - id of habit which we enroll
     * @return {@link HabitStatusDto}
     */
    @Override
    public HabitStatusDto enrollHabit(Long habitId, Long userId) {
        HabitStatus habitStatus = habitStatusRepo.findByHabitIdAndUserId(habitId, userId);
        int workingDays = habitStatus.getWorkingDays();
        habitStatus.setWorkingDays(++workingDays);
        LocalDateTime todayDate = LocalDateTime.now();
        HabitStatusCalendar habitCalendar;
        LocalDateTime lastEnrollmentDate =
            habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatus);
        long intervalBetweenDates = 0;
        if (lastEnrollmentDate != null) {
            intervalBetweenDates = Duration.between(lastEnrollmentDate, todayDate).toHours();
        }

        if (lastEnrollmentDate == null) {
            int habitStreak = habitStatus.getHabitStreak();
            habitStatus.setHabitStreak(++habitStreak);
            habitCalendar = new HabitStatusCalendar(todayDate, habitStatus);
            habitStatusCalendarService.save(habitCalendar);
        } else if (intervalBetweenDates >= 20 && intervalBetweenDates <= 24) {
            int habitStreak = habitStatus.getHabitStreak();
            habitStatus.setHabitStreak(++habitStreak);
            habitCalendar = new HabitStatusCalendar(todayDate, habitStatus);
            habitStatusCalendarService.save(habitCalendar);
        } else if (intervalBetweenDates > 24) {
            habitStatus.setHabitStreak(1);
            habitCalendar = new HabitStatusCalendar(todayDate, habitStatus);
            habitStatusCalendarService.save(habitCalendar);
        } else if (intervalBetweenDates < 19) {
            throw new BadRequestException(ErrorMessage.HABIT_HAS_BEEN_ALREADY_ENROLLED);
        }

        habitStatus.setLastEnrollmentDate(LocalDateTime.now());

        return modelMapper.map(habitStatusRepo.save(habitStatus), HabitStatusDto.class);
    }

    /**
     * Method unenroll Habit in defined date
     * @param habitId - id of habit
     * @param dateTime - date we want unenroll
     */
    @Override
    public void unenrollHabit(LocalDateTime dateTime, Long habitId, Long userId) {
        HabitStatus habitStatus = habitStatusRepo.findByHabitIdAndUserId(habitId, userId);

        List<LocalDateTime> enrollDates = habitStatusCalendarService.findEnrolledDatesAfter(dateTime, habitStatus);
        Collections.sort(enrollDates);
        int daysStreak = 0;

        for (int i = 0; i < enrollDates.size() - 1; i++) {
            if (Duration.between(enrollDates.get(i), enrollDates.get(i + 1)).toHours() >= 0 &&
                Duration.between(enrollDates.get(i), enrollDates.get(i + 1)).toHours() < 24) {
                daysStreak++;
            } else {
                daysStreak = 0;
            }
        }

        habitStatus.setHabitStreak(daysStreak + 1);
        habitStatus.setWorkingDays(habitStatus.getWorkingDays() - 1);
        habitStatusRepo.save(habitStatus);

        HabitStatusCalendar habitStatusCalendar =
            habitStatusCalendarService
                .findByEnrollDateIsBetweenAndHabitStatus(dateTime, dateTime.plusDays(1), habitStatus);

        if (habitStatusCalendar != null) {
            habitStatusCalendarService.delete(habitStatusCalendar);
        }
    }

    /**
     * Method enroll habit for defined date
     * @param habitId - id of habit
     * @param date - date we want enroll
     */
    @Override
    public void enrollHabitInDate(Long habitId, Long userId, LocalDateTime date) {
        HabitStatus habitStatus = habitStatusRepo.findByHabitIdAndUserId(habitId, userId);

        HabitStatusCalendar habitCalendarOnDate =
            habitStatusCalendarService.findByEnrollDateIsBetweenAndHabitStatus(date, date.plusDays(1), habitStatus);

        if (habitCalendarOnDate == null) {
            HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar(date, habitStatus);
            habitStatusCalendarService.save(habitStatusCalendar);
        }
    }
}