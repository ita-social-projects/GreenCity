package greencity.service;

import greencity.dto.PageableAdvancedDto;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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
     * Method that allow you to find not 'DEACTIVATED' {@link User} by email.
     *
     * @param email - {@link User}'s email
     * @return {@link Optional} of found {@link User}.
     * @author Vasyl Zhovnir
     */
    Optional<User> findNotDeactivatedByEmail(String email);

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
    PageableDto<UserForListDto> findByPage(Pageable pageable);

    /**
     * Find {@link User} for management by page .
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@link PageableAdvancedDto}.
     * @author Vasyl Zhovnir
     */
    PageableAdvancedDto<UserManagementDto> findUserForManagementByPage(Pageable pageable);

    /**
     * Method that allows you to update {@link User} by dto.
     *
     * @param dto - dto {@link UserManagementDto} with updated fields for updating {@link User}.
     * @author Vasyl Zhovnir
     */
    void updateUser(UserManagementDto dto);


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
     * @param userId   id of the {@link User} current user.
     * @param language needed language code.
     * @return List of {@link UserGoalDto}.
     */
    List<UserGoalResponseDto> getUserGoals(Long userId, String language);

    /**
     * Method returns list of available (not ACTIVE) goals for user for specific language.
     *
     * @param userId   id of the {@link User} current user.
     * @param language needed language code.
     * @return List of {@link GoalDto}.
     */
    List<GoalDto> getAvailableGoals(Long userId, String language);

    /**
     * Method saves list of user goals.
     *
     * @param userId   id of the {@link User} current user.
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
     * @param userId   id of the {@link User} current user.
     * @param goalId   - {@link UserGoal}'s id that should be updated.
     * @param language needed language code.
     * @return {@link UserGoalDto} with specific language.
     */
    UserGoalResponseDto updateUserGoalStatus(Long userId, Long goalId, String language);

    /**
     * Method returns list of available (not ACTIVE) habitDictionary for user.
     *
     * @param userId   id of the {@link User} current user.
     * @param language language code.
     * @return List of {@link HabitDictionaryDto}
     * @author Bogdan Kuzenko
     */
    List<HabitDictionaryDto> getAvailableHabitDictionary(Long userId, String language);

    /**
     * Method returns list of available habit for user.
     *
     * @param userId     id of the {@link User} current user.
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

    /**
     * Get profile picture path {@link String}.
     *
     * @return profile picture path {@link String}
     */
    String getProfilePicturePathByUserId(Long id);

    /**
     * Update user profile picture {@link User}.
     *
     * @param image                 {@link MultipartFile}
     * @param email                 {@link String} - email of user that need to update.
     * @param userProfilePictureDto {@link UserProfilePictureDto}
     * @return {@link User}.
     * @author Marian Datsko
     */
    User updateUserProfilePicture(MultipartFile image, String email,
                                  UserProfilePictureDto userProfilePictureDto);

    /**
     * Get list user friends by user id {@link User}.
     *
     * @param userId {@link Long}
     * @return {@link User}.
     * @author Marian Datsko
     */
    List<User> getAllUserFriends(Long userId);

    /**
     * Delete user friend by id {@link User}.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    void deleteUserFriendById(Long userId, Long friendId);

    /**
     * Add new user friend {@link User}.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    void addNewFriend(Long userId, Long friendId);

    /**
     * Get six friends with the highest rating {@link User}.
     *
     * @param userId {@link Long}
     * @author Marian Datsko
     */
    List<UserProfilePictureDto> getSixFriendsWithTheHighestRating(Long userId);

    /**
     * Save user profile information {@link User}.
     *
     * @author Marian Datsko
     */
    UserProfileDtoResponse saveUserProfile(UserProfileDtoRequest userProfileDtoRequest, String name);

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link User}'s id
     * @param userLastActivityTime - new {@link User}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    void updateUserLastActivityTime(Long userId, Date userLastActivityTime);

    /**
     * The method checks by id if a {@link User} is online.
     *
     * @param userId - {@link User}'s id
     * @author Yurii Zhurakovskyi
     */
    boolean checkIfTheUserIsOnline(Long userId);

    /**
     * Method return user profile information {@link User}.
     *
     * @param userId - {@link User}'s id
     * @author Marian Datsko
     */
    UserProfileDtoResponse getUserProfileInformation(Long userId);

    /**
     * Method return user profile statistics {@link User}.
     *
     * @param userId - {@link User}'s id
     * @author Marian Datsko
     */
    UserProfileStatisticsDto getUserProfileStatistics(Long userId);

    /**
     * Get user and six friends with the online status {@link User}.
     *
     * @param userId {@link Long}
     * @author Yurii Zhurakovskyi
     */
    UserAndFriendsWithOnlineStatusDto getUserAndSixFriendsWithOnlineStatus(Long userId);

    /**
     * Get user and all friends with the online status {@link User} by page.
     *
     * @param userId {@link Long}
     * @author Yurii Zhurakovskyi
     */
    UserAndAllFriendsWithOnlineStatusDto getAllFriendsWithTheOnlineStatus(Long userId, Pageable pageable);

    /**
     * change {@link User}'s status to DEACTIVATED.
     *
     * @param id {@link User}'s id
     * @author Vasyl Zhovnir
     */
    void deactivateUser(Long id);

    /**
     * Method deactivates all the {@link User} by list of IDs.
     *
     * @param listId {@link List} of {@link User}s` ids to be deactivated
     * @return {@link List} of {@link User}s` ids
     * @author Vasyl Zhovnir
     */
    List<Long> deactivateAllUsers(List<Long> listId);

    /**
     * change {@link User}'s status to ACTIVATED.
     *
     * @param id {@link User}'s id
     * @author Vasyl Zhovnir
     */
    void setActivatedStatus(Long id);

    /**
     * Method that allow you to find {@link User} by ID and token.
     *
     * @param userId - {@link User}'s id
     * @param token - {@link User}'s token
     * @return {@link Optional} of {@link User}
     */
    Optional<User> findByIdAndToken(Long userId, String token);

    /**
     * Method for getting User by search query.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search,
     * @return {@link PageableAdvancedDto} of {@link UserManagementDto} instances.
     */
    PageableAdvancedDto<UserManagementDto> searchBy(Pageable paging, String query);
}
