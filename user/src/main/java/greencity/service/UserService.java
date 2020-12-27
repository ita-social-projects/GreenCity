package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.friends.SixFriendsPageResponceDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.user.*;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Provides the interface to manage {UserVO} entity.
 *
 * @author Nazar Stasyuk and Rostyslav && Yurii Koval
 * @version 1.0
 */
public interface UserService {
    /**
     * Method that allow you to save new {@link UserVO}.
     *
     * @param user a value of {@link UserVO}
     * @author Yurii Koval
     */
    UserVO save(UserVO user);

    /**
     * Method that allow you to find {@link UserVO} by ID.
     *
     * @param id a value of {@link Long}
     * @return {@link UserVO}
     */
    UserVO findById(Long id);

    /**
     * Method that allow you to find {@link UserVO} by ID.
     *
     * @param id a value of {@link Long}
     * @return {@link UserVO}
     */
    UserVOAchievement findUserForAchievement(Long id);

    /**
     * Method that allow you to delete {@link UserVO} by ID.
     *
     * @param id a value of {@link Long}
     */
    void deleteById(Long id);

    /**
     * Method that allow you to find {@link UserVO} by email.
     *
     * @param email a value of {@link String}
     * @return {@link UserVO} with this email.
     */
    UserVO findByEmail(String email);

    /**
     * Method that allow you to find not 'DEACTIVATED' {@link UserVO} by email.
     *
     * @param email - {@link UserVO}'s email
     * @return {@link Optional} of found {@link UserVO}.
     * @author Vasyl Zhovnir
     */
    Optional<UserVO> findNotDeactivatedByEmail(String email);

    /**
     * Find UserVO's id by UserVO email.
     *
     * @param email - {@link UserVO} email
     * @return {@link UserVO} id
     * @author Zakhar Skaletskyi
     */
    Long findIdByEmail(String email);

    /**
     * Update {@code ROLE} of user.
     *
     * @param id   {@link UserVO} id.
     * @param role {@link Role} for user.
     * @return {@link UserRoleDto}
     * @author Rostyslav Khasanov
     */
    UserRoleDto updateRole(Long id, Role role, String email);

    /**
     * Update status of user.
     *
     * @param id         {@link UserVO} id.
     * @param userStatus {@link UserStatus} for user.
     * @return {@link UserStatusDto}
     * @author Rostyslav Khasanov
     */
    UserStatusDto updateStatus(Long id, UserStatus userStatus, String email);

    /**
     * Find {@link UserVO}-s by page .
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@link PageableDto}.
     * @author Rostyslav Khasanov
     */
    PageableDto<UserForListDto> findByPage(Pageable pageable);

    /**
     * Find {@link UserVO} for management by page .
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@link PageableAdvancedDto}.
     * @author Vasyl Zhovnir
     */
    PageableAdvancedDto<UserManagementDto> findUserForManagementByPage(Pageable pageable);

    /**
     * Method that allows you to update {@link UserVO} by dto.
     *
     * @param dto - dto {@link UserManagementDto} with updated fields for updating
     *            {@link UserVO}.
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
     * Get list of available {@link EmailNotification} statuses for {@link UserVO}.
     *
     * @return available {@link EmailNotification} statuses.
     */
    List<EmailNotification> getEmailNotificationsStatuses();

    /**
     * Update last visit of user.
     *
     * @return {@link UserVO}.
     */
    UserVO updateLastVisit(UserVO user);

    /**
     * Find users by filter.
     *
     * @param filterUserDto contains objects whose values determine the filter
     *                      parameters of the returned list.
     * @param pageable      pageable configuration.
     * @return {@link PageableDto}.
     * @author Rostyslav Khasanov.
     */
    PageableDto<UserForListDto> getUsersByFilter(FilterUserDto filterUserDto, Pageable pageable);

    /**
     * Get {@link UserVO} dto by principal (email).
     *
     * @param email - email of user.
     * @return {@link UserUpdateDto}.
     * @author Nazar Stasyuk
     */
    UserUpdateDto getUserUpdateDtoByEmail(String email);

    /**
     * Update {@link UserVO}.
     *
     * @param dto   {@link UserUpdateDto} - dto with new {@link UserVO} params.
     * @param email {@link String} - email of user that need to update.
     * @return {@link UserVO}.
     * @author Nazar Stasyuk
     */
    UserUpdateDto update(UserUpdateDto dto, String email);

    /**
     * Updates refresh token for a given user.
     *
     * @param refreshTokenKey - new refresh token key
     * @param id              - user's id
     * @return - number of updated rows
     */
    int updateUserRefreshToken(String refreshTokenKey, Long id);

    /**
     * Method returns list of available (not ACTIVE) customGoals for user.
     *
     * @param userId id of the {@link UserVO} current user.
     * @return List of {@link CustomGoalResponseDto}
     * @author Bogdan Kuzenko
     */
    List<CustomGoalResponseDto> getAvailableCustomGoals(Long userId, String accessToken);

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
     * Update user profile picture {@link UserVO}.
     *
     * @param image                 {@link MultipartFile}
     * @param email                 {@link String} - email of user that need to
     *                              update.
     * @param userProfilePictureDto {@link UserProfilePictureDto}
     * @return {@link UserVO}.
     * @author Marian Datsko
     */
    UserVO updateUserProfilePicture(MultipartFile image, String email,
        UserProfilePictureDto userProfilePictureDto, String accessToken);

    /**
     * Delete user profile picture {@link UserVO}.
     *
     * @param email {@link String} - email of user that need to update.
     */
    void deleteUserProfilePicture(String email);

    /**
     * Get list user friends by user id {@link UserVO}.
     *
     * @param userId {@link Long}
     * @return {@link UserVO}.
     * @author Marian Datsko
     */
    List<UserVO> getAllUserFriends(Long userId);

    /**
     * Delete user friend by id {@link UserVO}.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    void deleteUserFriendById(Long userId, Long friendId);

    /**
     * Add new user friend {@link UserVO}.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    void addNewFriend(Long userId, Long friendId);

    /**
     * Get six friends with the highest rating {@link UserVO}.
     *
     * @param userId {@link Long}
     * @author Marian Datsko
     */
    List<UserProfilePictureDto> getSixFriendsWithTheHighestRating(Long userId);

    /**
     * Get six friends with the highest rating {@link UserVO}. by page.
     *
     * @param userId {@link Long}
     * @return {@link SixFriendsPageResponceDto}.
     * @author Oleh Bilonizhka
     */
    SixFriendsPageResponceDto getSixFriendsWithTheHighestRatingPaged(Long userId);

    /**
     * Save user profile information {@link UserVO}.
     *
     * @author Marian Datsko
     */
    UserProfileDtoResponse saveUserProfile(UserProfileDtoRequest userProfileDtoRequest, String name,
        String accessToken);

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link UserVO}'s id
     * @param userLastActivityTime - new {@link UserVO}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    void updateUserLastActivityTime(Long userId, Date userLastActivityTime);

    /**
     * The method checks by id if a {@link UserVO} is online.
     *
     * @param userId - {@link UserVO}'s id
     * @author Yurii Zhurakovskyi
     */
    boolean checkIfTheUserIsOnline(Long userId);

    /**
     * Method return user profile information {@link UserVO}.
     *
     * @param userId - {@link UserVO}'s id
     * @author Marian Datsko
     */
    UserProfileDtoResponse getUserProfileInformation(Long userId);

    /**
     * Method return user profile statistics {@link UserVO}.
     *
     * @param userId - {@link UserVO}'s id
     * @author Marian Datsko
     */
    UserProfileStatisticsDto getUserProfileStatistics(Long userId, String accessToken);

    /**
     * Get user and six friends with the online status {@link UserVO}.
     *
     * @param userId {@link Long}
     * @author Yurii Zhurakovskyi
     */
    UserAndFriendsWithOnlineStatusDto getUserAndSixFriendsWithOnlineStatus(Long userId);

    /**
     * Get user and all friends with the online status {@link UserVO} by page.
     *
     * @param userId {@link Long}
     * @author Yurii Zhurakovskyi
     */
    UserAndAllFriendsWithOnlineStatusDto getAllFriendsWithTheOnlineStatus(Long userId, Pageable pageable);

    /**
     * change {@link UserVO}'s status to DEACTIVATED.
     *
     * @param id {@link UserVO}'s id
     * @author Vasyl Zhovnir
     */
    void deactivateUser(Long id);

    /**
     * Method deactivates all the {@link UserVO} by list of IDs.
     *
     * @param listId {@link List} of {@link UserVO}s` ids to be deactivated
     * @return {@link List} of {@link UserVO}s` ids
     * @author Vasyl Zhovnir
     */
    List<Long> deactivateAllUsers(List<Long> listId);

    /**
     * change {@link UserVO}'s status to ACTIVATED.
     *
     * @param id {@link UserVO}'s id
     * @author Vasyl Zhovnir
     */
    void setActivatedStatus(Long id);

    /**
     * Method that allow you to find {@link UserVO} by ID and token.
     *
     * @param userId - {@link UserVO}'s id
     * @param token  - {@link UserVO}'s token
     * @return {@link Optional} of {@link UserVO}
     */
    Optional<UserVO> findByIdAndToken(Long userId, String token);

    /**
     * Method for getting UserVO by search query.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search,
     * @return {@link PageableAdvancedDto} of {@link UserManagementDto} instances.
     */
    PageableAdvancedDto<UserManagementDto> searchBy(Pageable paging, String query);

    /**
     * Method for getting all Users.
     *
     * @return {@link List} of {@link UserVO} instances.
     */
    List<UserVO> findAll();

    /**
     * Method that finds user's friends by id.
     *
     * @return {@link List} of {@link UserManagementDto} instances.
     */
    List<UserManagementDto> findUserFriendsByUserId(Long id);

    /**
     * Method that finds user's recommended friends.
     *
     * @param pageable {@link Pageable}.
     * @param userId   {@link Long} -current user's id.
     * @return {@link PageableDto} of {@link RecommendedFriendDto} instances.
     */

    PageableDto<RecommendedFriendDto> findUsersRecommendedFriends(Pageable pageable, Long userId);

    /**
     * Method that finds all user's friends.
     *
     * @param pageable {@link Pageable}.
     * @param userId   {@link Long} -current user's id.
     * @return {@link PageableDto} of {@link RecommendedFriendDto} instances.
     */

    PageableDto<RecommendedFriendDto> findAllUsersFriends(Pageable pageable, Long userId);
}
