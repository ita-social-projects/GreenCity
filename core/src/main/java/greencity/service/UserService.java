package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.dto.habitstatistic.HabitIdDto;
import greencity.dto.user.*;
import greencity.entity.User;
import greencity.entity.UserGoal;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {@link User} entity.
 *
 * @author Nazar Stasyuk and Rostyslav && Yurii Koval
 * @version 1.0
 */
public interface UserService {
    /**
     * Method that allow you to save new {@link User}.
     *
     * @param user a value of {@link User}
     * @author Yurii Koval
     */
    User save(User user);

    /**
     * Method that allow you to find {@link User} by ID.
     *
     * @param id a value of {@link Long}
     * @return {@link User}
     */
    User findById(Long id);

    /**
     * Method that allow you to delete {@link User} by ID.
     *
     * @param id a value of {@link Long}
     */
    void deleteById(Long id);

    /**
     * Method that allow you to find {@link User} by email.
     *
     * @param email a value of {@link String}
     * @return {@link User} with this email.
     */
    User findByEmail(String email);

    /**
     * Find User's id by User email.
     *
     * @param email - {@link User} email
     * @return {@link User} id
     * @author Zakhar Skaletskyi
     */
    Long findIdByEmail(String email);

    /**
     * Update {@code ROLE} of user.
     *
     * @param id   {@link User} id.
     * @param role {@link ROLE} for user.
     * @return {@link UserRoleDto}
     * @author Rostyslav Khasanov
     */
    UserRoleDto updateRole(Long id, ROLE role, String email);

    /**
     * Update status of user.
     *
     * @param id         {@link User} id.
     * @param userStatus {@link UserStatus} for user.
     * @return {@link UserStatusDto}
     * @author Rostyslav Khasanov
     */
    UserStatusDto updateStatus(Long id, UserStatus userStatus, String email);

    /**
     * Find {@link User}-s by page .
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@link PageableDto}.
     * @author Rostyslav Khasanov
     */
    PageableDto findByPage(Pageable pageable);

    /**
     * Get all exists roles.
     *
     * @return {@link RoleDto}.
     * @author Rostyslav Khasanov
     */
    RoleDto getRoles();

    /**
     * Get list of available {@link EmailNotification} statuses for {@link User}.
     *
     * @return available {@link EmailNotification}  statuses.
     */
    List<EmailNotification> getEmailNotificationsStatuses();

    /**
     * Update last visit of user.
     *
     * @return {@link User}.
     */
    User updateLastVisit(User user);

    /**
     * Find users by filter.
     *
     * @param filterUserDto contains objects whose values determine the filter parameters of the returned list.
     * @param pageable      pageable configuration.
     * @return {@link PageableDto}.
     * @author Rostyslav Khasanov.
     */
    PageableDto<UserForListDto> getUsersByFilter(FilterUserDto filterUserDto, Pageable pageable);

    /**
     * Get {@link User} dto by principal (email).
     *
     * @param email - email of user.
     * @return {@link UserUpdateDto}.
     * @author Nazar Stasyuk
     */
    UserUpdateDto getUserUpdateDtoByEmail(String email);

    /**
     * Update {@link User}.
     *
     * @param dto   {@link UserUpdateDto} - dto with new {@link User} params.
     * @param email {@link String} - email of user that need to update.
     * @return {@link User}.
     * @author Nazar Stasyuk
     */
    User update(UserUpdateDto dto, String email);

    /**
     * Updates refresh token for a given user.
     *
     * @param refreshTokenKey - new refresh token key
     * @param id              - user's id
     * @return - number of updated rows
     */
    int updateUserRefreshToken(String refreshTokenKey, Long id);

    /**
     * Method returns list of user goals for specific language.
     *
     * @param userId     id of the {@link User} current user.
     * @param language needed language code.
     * @return List of {@link UserGoalDto}.
     */
    List<UserGoalResponseDto> getUserGoals(Long userId, String language);

    /**
     * Method returns list of available (not ACTIVE) goals for user for specific language.
     *
     * @param userId     id of the {@link User} current user.
     * @param language needed language code.
     * @return List of {@link GoalDto}.
     */
    List<GoalDto> getAvailableGoals(Long userId, String language);

    /**
     * Method saves list of user goals.
     *
     * @param userId     id of the {@link User} current user.
     * @param language needed language code.
     * @return List of saved {@link UserGoalDto} with specific language.
     */
    List<UserGoalResponseDto> saveUserGoals(Long userId, BulkSaveUserGoalDto dto, String language);

    /**
     * Method for deleted list of user goals.
     *
     * @param ids string with ids object for deleting.
     * @return list ids of deleted {@link UserGoal}
     * @author Bogdan Kuzenko
     */
    List<Long> deleteUserGoals(String ids);

    /**
     * Method update status of user goal.
     *
     * @param userId     id of the {@link User} current user.
     * @param goalId   - {@link UserGoal}'s id that should be updated.
     * @param language needed language code.
     * @return {@link UserGoalDto} with specific language.
     */
    UserGoalResponseDto updateUserGoalStatus(Long userId, Long goalId, String language);

    /**
     * Method returns list of available (not ACTIVE) habitDictionary for user.
     *
     * @param userId id of the {@link User} current user.
     * @param language language code.
     * @return List of {@link HabitDictionaryDto}
     * @author Bogdan Kuzenko
     */
    List<HabitDictionaryDto> getAvailableHabitDictionary(Long userId, String language);

    /**
     * Method returns list of available habit for user.
     *
     * @param userId       id of the {@link User} current user.
     * @param habitIdDto {@link HabitIdDto}
     * @return List of {@link HabitCreateDto}
     */
    List<HabitCreateDto> createUserHabit(Long userId, List<HabitIdDto> habitIdDto, String language);

    /**
     * Method delete habit fot user.
     *
     * @param userId      id current user.
     * @param habitIdDtos {@link HabitIdDto}
     */
    void deleteHabitByUserIdAndHabitDictionary(Long userId, Long habitIdDtos);

    /**
     * Method add default habit.
     *
     * @param userId id of the current user
     */
    void addDefaultHabit(Long userId, String language);

    /**
     * Method returns list of available (not ACTIVE) customGoals for user.
     *
     * @param userId id of the {@link User} current user.
     * @return List of {@link CustomGoalResponseDto}
     * @author Bogdan Kuzenko
     */
    List<CustomGoalResponseDto> getAvailableCustomGoals(Long userId);

    /**
     * Counts all users by user {@link UserStatus} ACTIVATED.
     *
     * @return amount of users with {@link UserStatus} ACTIVATED.
     * @author Shevtsiv Rostyslav
     */
    long getActivatedUsersAmount();
}
