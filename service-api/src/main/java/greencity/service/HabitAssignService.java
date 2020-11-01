package greencity.service;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.user.UserVO;
import java.util.List;

public interface HabitAssignService {
    /**
     * Method to find {@code HabitAssign} by id.
     *
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto getById(Long habitAssignId);

    /**
     * Method for assigning {@code Habit}.
     *
     * @param habitId {@code Habit} id.
     * @param user    @link UserVO} id.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto assignHabitForUser(Long habitId, UserVO user);

    /**
     * Method to find {@code HabitAssign} by {@code Habit} id and {@code User} id
     * (not suspended).
     *
     * @param userId  {@code User} id.
     * @param habitId {@code Habit} id.
     * @return HabitAssignDto.
     */
    HabitAssignDto findActiveHabitAssignByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Method to find all active (not suspended) {@code HabitAssign}'s by {@code User} id and acquired status.
     *
     * @param userId   {@code User} id.
     * @param acquired {@link Boolean} status.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllHabitAssignsByUserIdAndAcquiredStatus(Long userId, Boolean acquired);

    /**
     * Method to find all active (not suspended) {@code HabitAssign}'s by {@code Habit} id and acquired status.
     *
     * @param habitId   {@code Habit} id.
     * @param acquired {@link Boolean} status.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllHabitAssignsByHabitIdAndAcquiredStatus(Long habitId, Boolean acquired);

    /**
     * Method to delete all {@code HabitAssign}'s by {@code Habit} instance.
     *
     * @param habit HabitVO instance.
     */
    void deleteAllHabitAssignsByHabit(HabitVO habit);

    /**
     * Method for updating {@code HabitAssign} in database.
     *
     * @param dto - dto with {@code HabitAssign} suspended and acquired status.
     * @return {@link HabitAssignDto} instance.
     */
    HabitAssignDto updateStatus(Long habitAssignId, HabitAssignStatDto dto);
}
