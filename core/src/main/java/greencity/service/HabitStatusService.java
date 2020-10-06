package greencity.service;

import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatus.UpdateHabitStatusDto;
import greencity.entity.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface HabitStatusService {
    /**
     * Method to find {@link HabitStatus} by id.
     *
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto getById(Long id);

    /**
     * Method save {@link HabitStatus} by {@link HabitAssign}.
     *
     * @param habitAssign target {@link HabitAssign}.
     */
    void saveStatusByHabitAssign(HabitAssign habitAssign);

    /**
     * Find active {@link HabitStatus} by {@link HabitAssign} id.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto findActiveStatusByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Find active {@link HabitStatus} by {@link HabitAssign} id.
     *
     * @param habitAssignId target {@link HabitAssign} id.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto findStatusByHabitAssignId(Long habitAssignId);

    /**
     * Find {@link HabitStatus} by {@link Habit} and {@link User} id's.
     *
     * @param userId   {@link User} id.
     * @param habitId  {@link Habit} id.
     * @param dateTime {@link ZonedDateTime} dateTime.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto findStatusByUserIdAndHabitIdAndCreateDate(Long userId, Long habitId, ZonedDateTime dateTime);

    /**
     * Method enroll {@link greencity.entity.Habit}.
     *
     * @param habitAssignId - id of {@link HabitAssign} which we enroll.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto enrollHabit(Long habitAssignId);

    /**
     * Method to unenroll Habit in defined date.
     *
     * @param habitAssignId - id of {@link Habit}.
     * @param dateTime      - {@link LocalDate} dateTime we want unenroll.
     */
    void unenrollHabit(LocalDate dateTime, Long habitAssignId);

    /**
     * Method to enroll habit for defined date.
     *
     * @param habitAssignId - id of {@link HabitAssign}.
     * @param date          - {@link LocalDate} date we want enroll.
     */
    void enrollHabitInDate(Long habitAssignId, LocalDate date);

    /**
     * Method to delete {@link HabitStatus} by {@link HabitAssign} id.
     *
     * @param habitAssignId target {@link HabitAssign} id.
     */
    void deleteStatusByHabitAssignId(Long habitAssignId);

    /**
     * Method to delete active {@link HabitStatus} by {@link HabitAssign} id.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     */
    void deleteActiveStatusByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Method to delete {@link HabitStatus} by {@link User}, {@link Habit} id's
     * and {@link ZonedDateTime} dateTime from {@link HabitAssign}.
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     * @param dateTime  {@link ZonedDateTime} dateTime.
     */
    void deleteStatusByUserIdAndHabitIdAndAssignCreateDate(Long userId, Long habitId, ZonedDateTime dateTime);

    /**
     * Method for updating {@link HabitStatus} in database.
     *
     * @param dto - dto with {@link HabitStatus} id, rate and amount of items.
     * @return {@link UpdateHabitStatusDto} instance.
     */
    HabitStatusDto update(Long habitAssignId, UpdateHabitStatusDto dto);
}