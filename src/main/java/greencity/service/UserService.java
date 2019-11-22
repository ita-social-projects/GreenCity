package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.user.*;
import greencity.entity.User;
import greencity.entity.UserGoal;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {@link User} entity.
 *
 * @author Nazar Stasyuk and Rostyslav .....
 * @version 1.0
 */
public interface UserService {
    /**
     * Method that allow you to save new {@link User}.
     *
     * @param user a value of {@link User}
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
     * @return Optional of {@link User}
     */
    Optional<User> findByEmail(String email);

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
     * @param dto {@link UserUpdateDto} - dto with new {@link User} params.
     * @param email {@link String} - email of user that need to update.
     * @return {@link User}.
     * @author Nazar Stasyuk
     */
    User update(UserUpdateDto dto, String email);

    /**
     * Updates refresh token for a given user.
     *
     * @param refreshTokenKey - new refresh token key
     * @param id - user's id
     * @return - number of updated rows
     */
    int updateUserRefreshToken(String refreshTokenKey, Long id);
}

    /**
     * Method returns list of user goals.
     *
     * @param user {@link User} current user.
     * @return List of {@link UserGoalDto}.
     */
    List<UserGoalResponseDto> getUserGoals(User user);

    /**
     * Method returns list of available (not ACTIVE) goals for user.
     *
     * @param user {@link User} current user.
     * @return List of {@link GoalDto}.
     */
    List<GoalDto> getAvailableGoals(User user);

    /**
     * Method saves list of user goals.
     *
     * @param user {@link User} current user.
     * @return List of saved {@link UserGoalDto}.
     */
    List<UserGoalResponseDto> saveUserGoals(User user, BulkSaveUserGoalDto dto);

    /**
     * Method update status of user goal.
     *
     * @param user {@link User} current user.
     * @param goalId - {@link UserGoal}'s id that should be updated.
     * @return {@link UserGoalDto}
     */
    UserGoalResponseDto updateUserGoalStatus(User user, Long goalId);
}