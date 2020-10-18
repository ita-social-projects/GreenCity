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
     * @param habitAssignId target {@link HabitAssign} id.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto findStatusByHabitAssignId(Long habitAssignId);

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
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto enrollHabitInDate(Long habitAssignId, LocalDate date);

    /**
     * Method to delete {@link HabitStatus} by {@link HabitAssign} id.
     *
     * @param habitAssign {@link HabitAssign} instance.
     */
    void deleteStatusByHabitAssign(HabitAssign habitAssign);

    /**
     * Method for updating {@link HabitStatus} in database.
     *
     * @param dto - dto with {@link HabitStatus} working days, habitStreak, enrollment date.
     * @return {@link UpdateHabitStatusDto} instance.
     */
    HabitStatusDto update(Long habitAssignId, UpdateHabitStatusDto dto);
}
