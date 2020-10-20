package greencity.service;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignStatDto;
import java.time.ZonedDateTime;
import greencity.dto.user.UserVO;
import greencity.dto.habit.HabitVO;
import java.util.List;

public interface HabitAssignService {
    /**
     * Method to find HabitAssign by id.
     *
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto getById(Long habitAssignId);

    /**
     * Method for assigning Habit.
     *
     * @param habitId Habit id.
     * @param user    @link UserVO} id.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto assignHabitForUser(Long habitId, UserVO user);

    /**
     * Method to find HabitAssign by Habit id and User id
     * (not suspended).
     *
     * @param userId User id.
     * @param habitId Habit id.
     * @return HabitAssignDto.
     */
    HabitAssignDto findActiveHabitAssignByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Method to find HabitAssign by Habit id, User id
     * and {@link ZonedDateTime} dateTime (not suspended).
     *
     * @param userId   User id.
     * @param habitId  Habit id.
     * @param dateTime ZonedDateTime dateTime.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto findHabitAssignByUserIdAndHabitIdAndCreateDate(Long userId, Long habitId, ZonedDateTime dateTime);

    /**
     * Method to find all active (not suspended) HabitAssign by User id.
     *
     * @param userId User id.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllActiveHabitAssignsByUserId(Long userId);

    /**
     * Method to find all active (not suspended) HabitAssign by User id and acquired status.
     *
     * @param userId   User id.
     * @param acquired {@link Boolean} status.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllHabitAssignsByUserIdAndAcquiredStatus(Long userId, Boolean acquired);

    /**
     * Method to delete all HabitAssign's by Habit instance.
     *
     * @param habit HabitVO instance.
     */
    void deleteAllHabitAssignsByHabit(HabitVO habit);

    /**
     * Method for updating {HabitAssign} in database.
     *
     * @param dto - dto with {HabitAssign} suspended and acquired status.
     * @return {@link HabitAssignDto} instance.
     */
    HabitAssignDto updateStatus(Long habitAssignId, HabitAssignStatDto dto);
}
