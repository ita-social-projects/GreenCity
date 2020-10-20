package greencity.service;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignStatDto;

import greencity.dto.user.UserVO;
import greencity.dto.habit.HabitVO;
import java.time.ZonedDateTime;
import java.util.List;

public interface HabitAssignService {
    /**
     * Method to find {HabitAssign} by id.
     *
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto getById(Long habitAssignId);

    /**
     * Method for assigning {Habit} for user.
     *
     * @param habitId {Habit} id.
     * @param user    {@link UserVO} id.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto assignHabitForUser(Long habitId, UserVO user);

    /**
     * Method to find {HabitAssign} by {Habit} id and {User} id
     * (not suspended).
     *
     * @param userId  {User} id.
     * @param habitId {Habit} id.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto findActiveHabitAssignByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Method to find {HabitAssign} by {Habit} id, {User} id
     * and {@link ZonedDateTime} dateTime (not suspended).
     *
     * @param userId   {User} id.
     * @param habitId  {Habit} id.
     * @param dateTime {@link ZonedDateTime} dateTime.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto findHabitAssignByUserIdAndHabitIdAndCreateDate(Long userId, Long habitId, ZonedDateTime dateTime);

    /**
     * Method to find all active (not suspended) {HabitAssign} by {User} id.
     *
     * @param userId {User} id.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllActiveHabitAssignsByUserId(Long userId);

    /**
     * Method to find all active (not suspended) {HabitAssign} by {User} id and acquired status.
     *
     * @param userId   {User} id.
     * @param acquired {@link Boolean} status.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllHabitAssignsByUserIdAndAcquiredStatus(Long userId, Boolean acquired);

    /**
     * Method to suspend {HabitAssign} by {User} id and {Habit} id
     * (with not suspended status).
     *
     * @param habitId {Habit} id.
     * @param userId  {User} id.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto suspendHabitAssignByHabitIdAndUserId(Long habitId, Long userId);

    /**
     * Method to suspend {HabitAssign} by it's id.
     *
     * @param habitAssignId {HabitAssign} id.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto suspendHabitAssignById(Long habitAssignId);

    /**
     * Method for updating {HabitAssign} with new acquired status.
     *
     * @param id       {HabitAssign} id.
     * @param acquired {Boolean} status.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto updateHabitAssignAcquiredStatusById(Long id, Boolean acquired);

    /**
     * Method to delete {HabitAssign} by {User} and {Habit} id's
     * and {@link ZonedDateTime} dateTime.
     *
     * @param userId   {User} id.
     * @param habitId  {Habit} id.
     * @param dateTime {@link ZonedDateTime} dateTime.
     */
    void deleteHabitAssignByUserIdAndHabitIdAndCreateDate(Long userId, Long habitId, ZonedDateTime dateTime);

    /**
     * Method for getting amount of habits in progress by user id.
     *
     * @param id {@link Long} user id.
     * @return amount of habits in progress by user id.
     */
    Long getAmountOfHabitsInProgressByUserId(Long id);

    /**
     * Method for getting amount of acquired habits by user id.
     *
     * @param id {@link Long} user id.
     * @return amount of acquired habits by user id.
     */
    Long getAmountOfAcquiredHabitsByUserId(Long id);

    /**
     * Method for updating {HabitAssign} in database.
     *
     * @param dto - dto with {HabitAssign} suspended and acquired status.
     * @return {@link HabitAssignDto} instance.
     */
    HabitAssignDto updateStatus(Long habitAssignId, HabitAssignStatDto dto);
}
