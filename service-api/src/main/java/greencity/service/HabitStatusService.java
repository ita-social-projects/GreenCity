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
     * Method to enroll {@code Habit}.
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
     *
     * @param habitAssignId - id of {@code HabitAssign}.
     * @param date          - {@link LocalDate} date we want enroll.
     * @return {@link HabitStatusDto}.
     */
    HabitStatusDto enrollHabitInDate(Long habitAssignId, LocalDate date);

    /**
     * Method to delete {@code HabitStatus} by {@code HabitAssign} instance.
     *
     * @param habitAssign {@link HabitAssignVO} instance.
     */
    void deleteStatusByHabitAssign(HabitAssignVO habitAssign);

    /**
     * Method for updating {@code HabitStatus} in database.
     *
     * @param dto - dto with {@code HabitStatus} working days, habitStreak, enrollment date.
     * @return {@link UpdateHabitStatusDto} instance.
     */
    HabitStatusDto update(Long habitAssignId, UpdateHabitStatusDto dto);
}
