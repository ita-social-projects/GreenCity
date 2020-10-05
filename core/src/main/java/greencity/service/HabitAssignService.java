package greencity.service;

import greencity.dto.habit.HabitAssignDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import java.time.ZonedDateTime;
import java.util.List;

public interface HabitAssignService {
    /**
     * Method to find {@link HabitAssign} by id.
     *
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto getById(Long habitAssignId);

    /**
     * Method for assigning {@link Habit} for user.
     *
     * @param habitId {@link Habit} id.
     * @param user    {@link User} id.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto assignHabitForUser(Long habitId, User user);

    /**
     * Method to find {@link HabitAssign} by {@link Habit} id and {@link User} id
     * (not suspended).
     *
     * @param userId  {@link User} id.
     * @param habitId {@link Habit} id.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto findActiveHabitAssignByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Method to find {@link HabitAssign} by {@link Habit} id, {@link User} id
     * and {@link ZonedDateTime} dateTime (not suspended).
     *
     * @param userId   {@link User} id.
     * @param habitId  {@link Habit} id.
     * @param dateTime {@link ZonedDateTime} dateTime.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto findHabitAssignByUserIdAndHabitIdAndCreateDate(Long userId, Long habitId, ZonedDateTime dateTime);

    /**
     * Method to find all active (not suspended) {@link HabitAssign} by {@link User} id.
     *
     * @param userId {@link User} id.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllActiveHabitAssignsByUserId(Long userId);

    /**
     * Method to find all active (not suspended) {@link HabitAssign} by {@link User} id and acquired status.
     *
     * @param userId   {@link User} id.
     * @param acquired {@link Boolean} status.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllHabitAssignsByUserIdAndAcquiredStatus(Long userId, Boolean acquired);

    /**
     * Method to suspend {@link HabitAssign} by {@link User} id and {@link Habit} id
     * (with not suspended status).
     *
     * @param habitId {@link Habit} id.
     * @param userId  {@link User} id.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto suspendHabitAssignByHabitIdAndUserId(Long habitId, Long userId);

    /**
     * Method to suspend {@link HabitAssign} by it's id.
     *
     * @param habitAssignId {@link HabitAssign} id.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto suspendHabitAssignById(Long habitAssignId);

    /**
     * Method for updating {@link HabitAssign} with new acquired status.
     *
     * @param id       {@link HabitAssign} id.
     * @param acquired {@link Boolean} status.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto updateHabitAssignAcquiredStatusById(Long id, Boolean acquired);

    /**
     * Method to delete {@link HabitAssign} by {@link User} and {@link Habit} id's
     * and {@link ZonedDateTime} dateTime.
     *
     * @param userId   {@link User} id.
     * @param habitId  {@link Habit} id.
     * @param dateTime {@link ZonedDateTime} dateTime.
     */
    void deleteHabitAssignByUserIdAndHabitIdAndCreateDate(Long userId, Long habitId, ZonedDateTime dateTime);
}
