package greencity.service;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatus.UpdateHabitStatusDto;
import java.time.LocalDate;

public interface HabitStatusService {
    /**
     * Method to find {@code HabitStatus} by id.
     *
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto getById(Long id);

    /**
     * Method save {@code HabitStatus} by {@code HabitAssign}.
     *
     * @param habitAssign target {@link HabitAssignVO}.
     */
    void saveStatusByHabitAssign(HabitAssignVO habitAssign);

    /**
     * Find active {@code HabitStatus} by {@code Habit} and {@code User} id's.
     *
     * @param habitId {@code Habit} id.
     * @param userId  {@code User} id.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto findActiveStatusByHabitIdAndUserId(Long habitId, Long userId);

    /**
     * Find active {@code HabitStatus} by {@code HabitAssign} id.
     *
     * @param habitAssignId target {@code HabitAssign} id.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto findStatusByHabitAssignId(Long habitAssignId);

    /**
     * Method to enroll {@code Habit}.
     *
     * @param habitId {@code Habit} id to enroll.
     * @param userId  {@code User} id.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto enrollHabit(Long habitId, Long userId);

    /**
     * Method to unenroll Habit in defined date.
     *
     * @param habitId  {@code Habit} id to unenroll.
     * @param userId   {@code User} id.
     * @param dateTime {@link LocalDate} dateTime we want unenroll.
     */
    void unenrollHabit(Long habitId, Long userId, LocalDate dateTime);

    /**
     * Method to enroll habit for defined date.
     *
     * @param habitId {@code Habit} id to enroll.
     * @param userId  {@code User} id.
     * @param date    {@link LocalDate} date we want enroll.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto enrollHabitInDate(Long habitId, Long userId, LocalDate date);

    /**
     * Method to delete {@code HabitStatus} by {@code HabitAssign} instance.
     *
     * @param habitAssign {@link HabitAssignVO} instance.
     */
    void deleteStatusByHabitAssign(HabitAssignVO habitAssign);

    /**
     * Method for updating {@code HabitStatus} in database.
     *
     * @param habitId {@code Habit} id to update.
     * @param userId  {@code User} id.
     * @param dto     dto with {@code HabitStatus} working days, habitStreak,
     *                enrollment date.
     * @return {@link UpdateHabitStatusDto} instance.
     */
    HabitStatusDto update(Long habitId, Long userId, UpdateHabitStatusDto dto);
}
