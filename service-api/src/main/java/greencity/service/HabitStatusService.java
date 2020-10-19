package greencity.service;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatus.UpdateHabitStatusDto;
import java.time.LocalDate;
import java.time.ZonedDateTime;

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
     * @param habitAssign target {@link HabitAssignDto}.
     */
    void saveStatusByHabitAssign(HabitAssignDto habitAssign);

    /**
     * Find active {@code HabitStatus} by {@code HabitAssign} id.
     *
     * @param userId  {@code User} id.
     * @param habitId {@code Habit} id.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto findActiveStatusByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Find active {@code HabitStatus} by {@code HabitAssign} id.
     *
     * @param habitAssignId target {@code HabitAssign} id.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto findStatusByHabitAssignId(Long habitAssignId);

    /**
     * Find {@code HabitStatus} by {@code Habit} and {@code User} id's.
     *
     * @param userId   {@code User} id.
     * @param habitId  {@code Habit} id.
     * @param dateTime {@link ZonedDateTime} dateTime.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto findStatusByUserIdAndHabitIdAndCreateDate(Long userId, Long habitId, ZonedDateTime dateTime);

    /**
     * Method enroll {@code Habit}.
     *
     * @param habitAssignId - id of {@code HabitAssign} which we enroll.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto enrollHabit(Long habitAssignId);

    /**
     * Method to unenroll Habit in defined date.
     *
     * @param habitAssignId - id of {@code Habit}.
     * @param dateTime      - {@link LocalDate} dateTime we want unenroll.
     */
    void unenrollHabit(LocalDate dateTime, Long habitAssignId);

    /**
     * Method to enroll habit for defined date.
     *  @param habitAssignId - id of {@code HabitAssign}.
     * @param date          - {@link LocalDate} date we want enroll.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto enrollHabitInDate(Long habitAssignId, LocalDate date);

    /**
     * Method to delete {@code HabitStatus} by {@code HabitAssign} id.
     *
     * @param habitAssignId target {@code HabitAssign} id.
     */
    void deleteStatusByHabitAssignId(Long habitAssignId);

    /**
     * Method to delete active {@code HabitStatus} by {@code HabitAssign} id.
     *
     * @param userId  {@code User} id.
     * @param habitId {@code Habit} id.
     */
    void deleteActiveStatusByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Method to delete {@code HabitStatus} by {@code User}, {@code Habit} id's
     * and {@link ZonedDateTime} dateTime from {@code HabitAssign}.
     *
     * @param userId  {@code User} id.
     * @param habitId {@code Habit} id.
     * @param dateTime  {@link ZonedDateTime} dateTime.
     */
    void deleteStatusByUserIdAndHabitIdAndAssignCreateDate(Long userId, Long habitId, ZonedDateTime dateTime);

    /**
     * Method for updating {@code HabitStatus} in database.
     *
     * @param dto - dto with {@code HabitStatus} working days, habitStreak, enrollment date.
     * @return {@link UpdateHabitStatusDto} instance.
     */
    HabitStatusDto update(Long habitAssignId, UpdateHabitStatusDto dto);
}
