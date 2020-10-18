package greencity.service;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
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
     * Method to find all active (not suspended) {@link HabitAssign} by {@link User} id and acquired status.
     *
     * @param userId   {@link User} id.
     * @param acquired {@link Boolean} status.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllHabitAssignsByUserIdAndAcquiredStatus(Long userId, Boolean acquired);

    /**
     * Method to delete all {@link HabitAssign}'s by {@link Habit} instance.
     *
     * @param habit {@link Habit} instance.
     */
    void deleteAllHabitAssignsByHabit(Habit habit);

    /**
     * Method for updating {@link HabitAssign} in database.
     *
     * @param dto - dto with {@link HabitAssign} suspended and acquired status.
     * @return {@link HabitAssignDto} instance.
     */
    HabitAssignDto updateStatus(Long habitAssignId, HabitAssignStatDto dto);
}
