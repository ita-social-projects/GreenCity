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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HabitStatusServiceImpl implements HabitStatusService {
    private final HabitStatusRepo habitStatusRepo;
    private final HabitStatusCalendarService habitStatusCalendarService;
    private final ModelMapper modelMapper;

    /**
     * Method save {@link HabitStatusCalendar} for user.
     *
     * @param habit target habit
     * @param user  target habit
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
    }

    /**
     * Method delete {@link HabitStatus} by user.
     *
     * @param userId target userId
     */
    @Override
    public void deleteByUser(Long userId) {
        habitStatusRepo.deleteHabitStatusByUserId(userId);
    }

    /**
     * Find {@link HabitStatus} by habit and user id's.
     *
     * @param habitId target habit Id
     * @param userId  target user Id
     * @return {@link HabitStatusDto}
     */
    @Override
    public HabitStatusDto findStatusByHabitIdAndUserId(Long habitId, Long userId) {
        return modelMapper.map(habitStatusRepo.findByHabitIdAndUserId(habitId, userId), HabitStatusDto.class);
    }

    /**
     * Method enroll {@link greencity.entity.Habit}.
     *
     * @param habitId - id of habit which we enroll
     * @return {@link HabitStatusDto}
     */
    @Override
    public HabitStatusDto enrollHabit(Long habitId, Long userId) {
        HabitStatus habitStatus = habitStatusRepo.findByHabitIdAndUserId(habitId, userId);
        int workingDays = habitStatus.getWorkingDays();
        habitStatus.setWorkingDays(++workingDays);
        LocalDate todayDate = LocalDate.now();
        HabitStatusCalendar habitCalendar;
        LocalDate lastEnrollmentDate =
            habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatus);
        long intervalBetweenDates = 0;

        if (lastEnrollmentDate != null) {
            intervalBetweenDates = Period.between(lastEnrollmentDate, todayDate).getDays();
        }

        if ((intervalBetweenDates == 1) || lastEnrollmentDate == null) {
            int habitStreak = habitStatus.getHabitStreak();
            habitStatus.setHabitStreak(++habitStreak);
            habitCalendar = new HabitStatusCalendar(todayDate, habitStatus);
            habitStatusCalendarService.save(habitCalendar);
        } else if (intervalBetweenDates > 1) {
            habitStatus.setHabitStreak(1);
            habitCalendar = new HabitStatusCalendar(todayDate, habitStatus);
            habitStatusCalendarService.save(habitCalendar);
        } else {
            throw new BadRequestException(ErrorMessage.HABIT_HAS_BEEN_ALREADY_ENROLLED);
        }

        habitStatus.setLastEnrollmentDate(LocalDateTime.now());

        return modelMapper.map(habitStatusRepo.save(habitStatus), HabitStatusDto.class);
    }

    /**
     * Method unenroll Habit in defined date.
     *
     * @param habitId - id of habit
     * @param date    - date we want unenroll
     */
    @Override
    public void unenrollHabit(LocalDate date, Long habitId, Long userId) {
        HabitStatus habitStatus = habitStatusRepo.findByHabitIdAndUserId(habitId, userId);
        int daysStreak = checkHabitStreakAfterDate(date, habitStatus);
        habitStatus.setHabitStreak(daysStreak + 1);
        int workingDays = habitStatus.getWorkingDays();

        if (workingDays == 0) {
            habitStatus.setWorkingDays(0);
        } else {
            habitStatus.setWorkingDays(--workingDays);
        }

        habitStatusRepo.save(habitStatus);
        HabitStatusCalendar habitStatusCalendar =
            habitStatusCalendarService
                .findHabitStatusCalendarByEnrollDateAndHabitStatus(date, habitStatus);

        if (habitStatusCalendar != null) {
            habitStatusCalendarService.delete(habitStatusCalendar);
        } else {
            throw new BadRequestException(ErrorMessage.HABIT_IS_NOT_ENROLLED);
        }
    }

    /**
     * Method enroll habit for defined date.
     *
     * @param habitId - id of habit
     * @param date    - date we want enroll
     */
    @Override
    public void enrollHabitInDate(Long habitId, Long userId, LocalDate date) {
        HabitStatus habitStatus = habitStatusRepo.findByHabitIdAndUserId(habitId, userId);
        HabitStatusCalendar habitCalendarOnDate =
            habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(date, habitStatus);

        int daysStreakAfterDate = checkHabitStreakAfterDate(date, habitStatus);
        int daysStreakBeforeDate = checkHabitStreakBeforeDate(date, habitStatus);

        if (daysStreakBeforeDate != 0) {
            daysStreakBeforeDate += 1;
        }

        if (habitCalendarOnDate == null) {
            HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar(date, habitStatus);
            habitStatusCalendarService.save(habitStatusCalendar);

            if (Period.between(date, LocalDate.now()).getDays() == daysStreakAfterDate + 1) {
                if ((daysStreakAfterDate + daysStreakBeforeDate) == 1) {
                    habitStatus.setHabitStreak(daysStreakAfterDate + daysStreakBeforeDate + 1);
                } else {
                    habitStatus.setHabitStreak(daysStreakAfterDate + daysStreakBeforeDate + 2);
                }
            }

            habitStatus.setWorkingDays(habitStatus.getWorkingDays() + 1);
            habitStatusRepo.save(habitStatus);
        } else {
            throw new BadRequestException(ErrorMessage.HABIT_HAS_BEEN_ALREADY_IN_THAT_DAY);
        }
    }

    private int checkHabitStreakAfterDate(LocalDate dateTime, HabitStatus habitStatus) {
        int daysStreak = 0;

        List<LocalDate> enrollDates = habitStatusCalendarService.findEnrolledDatesAfter(dateTime, habitStatus);
        Collections.sort(enrollDates);

        for (int i = 0; i < enrollDates.size() - 1; i++) {
            if (Period.between(enrollDates.get(i), enrollDates.get(i + 1)).getDays() == 1) {
                daysStreak++;
            } else {
                daysStreak = 0;
            }
        }
        return daysStreak;
    }

    private int checkHabitStreakBeforeDate(LocalDate dateTime, HabitStatus habitStatus) {
        int daysStreak = 0;

        List<LocalDate> enrollDates = habitStatusCalendarService.findEnrolledDatesBefore(dateTime, habitStatus);
        Collections.sort(enrollDates);
        Collections.reverse(enrollDates);

        for (int i = 0; i < enrollDates.size() - 1; i++) {
            if (Period.between(enrollDates.get(i + 1), enrollDates.get(i)).getDays() == 1) {
                daysStreak++;
            } else {
                return daysStreak;
            }
        }

        return daysStreak;
    }
}
