package greencity.service;

import greencity.dto.habitstatus.HabitStatusDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatus;
import greencity.entity.User;
import java.time.LocalDateTime;
import java.util.Date;

public interface HabitStatusService {

    /**
     * Method save {@link HabitStatus} by habit
     * @param habit target habit
     * @param user target user
     */
    void saveByHabit(Habit habit, User user);

    /**
     * Method delete {@link HabitStatus} by user
     * @param userId target user id
     */
    void deleteByUser(Long userId);

    /**
     * Find {@link HabitStatus} by habit and user id's
     * @param habitId target habit Id
     * @param userId target user Id
     * @return
     */
    HabitStatusDto findStatusByHabitIdAndUserId(Long habitId, Long userId);

    /**
     * Method enroll {@link greencity.entity.Habit}
     * @param habitId - id of habit which we enroll
     * @return {@link HabitStatusDto}
     */
    HabitStatusDto enrollHabit(Long habitId, Long userId);

    /**
     * Method unenroll Habit in defined date
     * @param habitId - id of habit
     * @param dateTime - date we want unenroll
     */
    void unenrollHabit(LocalDateTime dateTime, Long habitId, Long userId);

    /**
     * Method enroll habit for defined date
     * @param habitId - id of habit
     * @param date - date we want enroll
     */
    void enrollHabitInDate(Long habitId, Long userId, LocalDateTime date);
}