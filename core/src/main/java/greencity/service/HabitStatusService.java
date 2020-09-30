package greencity.service;

import greencity.dto.habitstatus.HabitStatusDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatus;
import greencity.entity.User;
import java.time.LocalDate;

public interface HabitStatusService {
    /**
     * Method save {@link HabitStatus} by habit.
     *
     * @param habitAssign target habit
     */
    void saveByHabitAssign(HabitAssign habitAssign);

    /**
     * Method delete {@link HabitStatus} by user.
     *
     * @param habitAssignId target user id
     */
    void deleteStatusByHabitAssignId(Long habitAssignId);

    /**
     * Method delete {@link HabitStatus} by userId and habitId.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     */
    void deleteStatusByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Find {@link HabitStatus} by habitAssign id.
     *
     * @param habitAssignId target habitAssign Id
     * @return {@link HabitStatusDto}
     */
    HabitStatusDto findStatusByHabitAssignId(Long habitAssignId);

    /**
     * Find {@link HabitStatus} by habit and user id's.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     * @return {@link HabitStatusDto}
     */
    HabitStatusDto findStatusByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Method enroll {@link greencity.entity.Habit}.
     *
     * @param habitAssignId - id of habitAssign which we enroll
     * @return {@link HabitStatusDto}
     */
    HabitStatusDto enrollHabit(Long habitAssignId);

    /**
     * Method unenroll Habit in defined date.
     *
     * @param habitAssignId - id of habitAssign
     * @param dateTime      - date we want unenroll
     */
    void unenrollHabit(LocalDate dateTime, Long habitAssignId);

    /**
     * Method enroll habit for defined date.
     *
     * @param habitAssignId - id of habitAssign
     * @param date          - date we want enroll
     */
    void enrollHabitInDate(Long habitAssignId, LocalDate date);
}