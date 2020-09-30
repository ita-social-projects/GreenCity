package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.entity.*;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.exception.exceptions.WrongIdException;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class HabitStatusServiceImpl implements HabitStatusService {
    private final HabitStatusRepo habitStatusRepo;
    private final HabitStatusCalendarService habitStatusCalendarService;
    private final ModelMapper modelMapper;

    /**
     * Method save {@link HabitStatus} for user by {@link HabitAssign}.
     *
     * @param habitAssign target habitAssign
     */
    @Override
    public void saveByHabitAssign(HabitAssign habitAssign) {
        HabitStatus habitStatus = HabitStatus.builder()
            .habitStreak(0)
            .habitAssign(habitAssign)
            .workingDays(0)
            .lastEnrollmentDate(LocalDateTime.now())
            .build();

        habitStatusRepo.save(habitStatus);
    }

    /**
     * Method delete {@link HabitStatus} by habitAssignId.
     *
     * @param habitAssignId target userId
     */
    @Transactional
    @Override
    public void deleteStatusByHabitAssignId(Long habitAssignId) {
        habitStatusRepo.findByHabitAssignId(habitAssignId)
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.STATUS_OF_HABIT_NOT_DELETED));
        habitStatusRepo.deleteByHabitAssignId(habitAssignId);
    }

    /**
     * Method delete {@link HabitStatus} by habitAssign.
     *
     * @param userId  target userId
     * @param habitId target habitId
     */
    @Transactional
    @Override
    public void deleteStatusByUserIdAndHabitId(Long userId, Long habitId) {
        habitStatusRepo.findByUserIdAndHabitId(userId, habitId)
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.STATUS_OF_HABIT_NOT_DELETED));
        habitStatusRepo.deleteByUserIdAndHabitId(userId, habitId);
    }

    /**
     * Find {@link HabitStatus} by habitAssignId.
     *
     * @param habitAssignId target habitAssignId
     * @return {@link HabitStatusDto}
     */
    @Override
    public HabitStatusDto findStatusByHabitAssignId(Long habitAssignId) {
        return modelMapper.map(habitStatusRepo.findByHabitAssignId(habitAssignId)
                .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_HAS_NO_STATUS_FOR_SUCH_HABIT)),
            HabitStatusDto.class);
    }

    /**
     * Find {@link HabitStatus} by habit and user id's.
     *
     * @param userId  target userId
     * @param habitId target habitId
     * @return {@link HabitStatusDto}
     */
    @Override
    public HabitStatusDto findStatusByUserIdAndHabitId(Long userId, Long habitId) {
        return modelMapper.map(habitStatusRepo.findByUserIdAndHabitId(userId, habitId)
                .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_HAS_NO_STATUS_FOR_SUCH_HABIT)),
            HabitStatusDto.class);
    }

    /**
     * Method enroll {@link Habit}.
     *
     * @param habitAssignId - id of habitAssign which we enroll
     * @return {@link HabitStatusDto}
     */
    @Override
    public HabitStatusDto enrollHabit(Long habitAssignId) {
        HabitStatus habitStatus = habitStatusRepo.findByHabitAssignId(habitAssignId)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_HAS_NO_STATUS_FOR_SUCH_HABIT));
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
     * Method unenroll habit in defined date.
     *
     * @param habitAssignId - id of habitAssign
     * @param date          - date we want unenroll
     */
    @Override
    public void unenrollHabit(LocalDate date, Long habitAssignId) {
        HabitStatus habitStatus = habitStatusRepo.findByHabitAssignId(habitAssignId)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_HAS_NO_STATUS_FOR_SUCH_HABIT));
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
     * @param habitAssignId - id of habit
     * @param date          - date we want enroll
     */
    @Override
    public void enrollHabitInDate(Long habitAssignId, LocalDate date) {
        HabitStatus habitStatus = habitStatusRepo.findByHabitAssignId(habitAssignId)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_HAS_NO_STATUS_FOR_SUCH_HABIT));
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