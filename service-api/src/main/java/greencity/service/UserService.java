package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDetailedDto;
import greencity.dto.location.UserLocationDto;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.dto.user.GreenCityUserProfileDtoResponse;
import greencity.dto.user.UpdateUserCredoDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserAddRatingExternalDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserFilterDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    /**
     * Method that allow you to find not 'DEACTIVATED' {@link UserVO} by email.
     *
     * @param email - {@link UserVO}'s email
     * @return {@link Optional} of found {@link UserVO}.
     */
    UserVO findNotDeactivatedByEmail(String email);

    /**
     * Method that allow you to find {@link UserVO} by id.
     *
     * @param id a value of {@link Long}
     * @return {@link UserVO} with this id.
     */
    UserVO findById(Long id);

    /**
     * Update status of user.
     *
     * @param id         {@link UserVO} id.
     * @param userStatus {@link UserStatus} for user.
     * @return {@link UserStatusDto}
     */
    UserStatusDto updateStatus(Long id, UserStatus userStatus, Long currentUserId);

    /**
     * Update {@code ROLE} of user.
     *
     * @param id   {@link UserVO} id.
     * @param role {@link Role} for user.
     * @return {@link UserRoleDto} updates like this on User entity should be
     *         handled in GreenCityUser via RestClient.
     */
    UserRoleDto updateRole(Long id, Role role, String email);

    /**
     * The method checks by id if a {@link UserVO} is online.
     *
     * @param userId - {@link UserVO}'s id
     * @return {boolean} is user online
     */
    boolean checkIfTheUserIsOnline(Long userId);

    /**
     * Method that returns {@link String} initials (first one or two letters of
     * name) of user.
     *
     * @param userId - {@link UserVO}'s id
     * @return {@link String} user's initials
     */
    String getInitialsById(Long userId);

    /**
     * Method that returns {@link List} of top 6 friends with highest rating.
     *
     * @param userId - {@link UserVO}'s id
     * @return {@link List} of {@link UserVO} instances
     */
    List<UserVO> getSixFriendsWithTheHighestRating(Long userId);

    /**
     * Method for updating user event organizer rating.
     *
     * @param userId - {@link UserVO}'s id
     * @param rate   - new user event organizer rating
     */
    void updateEventOrganizerRating(Long userId, Double rate);

    /**
     * Method that returns a paginated list of users filtered by specified criteria.
     *
     * @param request  request for searching related data
     * @param pageable pagination information including page number, size, and
     *                 sorting options.
     * @return a {@link PageableDetailedDto} containing a list of
     *         {@link UserManagementVO} filtered by the given criteria, role, and
     *         status, along with pagination details.
     * @author Anton Bondar
     */
    PageableDetailedDto<UserManagementVO> getAllUsersByCriteria(UserFilterDto request, Pageable pageable);

    /**
     * Method that update user's rating.
     *
     * @param userId current user's id.
     * @param rating rating.
     */
    void updateUserRating(Long userId, Double rating);

    /**
     * Find and return city and coordinates .
     *
     * @param userId id of the user
     * @return {@link UserCityDto}
     **/
    UserCityDto findAllUsersCities(Long userId);

    /**
     * Find and return city and coordinates.
     *
     * @param email user's email
     * @return {@link UserCityDto}
     **/
    UserCityDto findAllUsersCities(String email);

    /**
     * Find and return user location by user id.
     *
     * @param userId id of the user
     * @return {@link UserLocationDto}
     **/
    UserLocationDto findUserLocationDtoByUserId(Long userId);

    /**
     * Find and return user location by user email.
     *
     * @param email user's email
     * @return {@link UserLocationDto}
     **/
    UserLocationDto findUserLocationDtoByEmail(String email);

    /**
     * Update user credo by user id.
     *
     * @param updateUserCredoDto containing update information
     **/
    void updateUserCredo(UpdateUserCredoDto updateUserCredoDto);

    /**
     * Set user location by coordinates from {@link UserProfileDtoRequest}.
     *
     * @param userId                id of the user whose location will be updated
     * @param userProfileDtoRequest contains location data
     */
    void setLocationForUser(Long userId, UserProfileDtoRequest userProfileDtoRequest);

    /**
     * Set user location by coordinates from {@link UserProfileDtoRequest}.
     *
     * @param email                 user's email
     * @param userProfileDtoRequest contains location data
     */
    void setLocationForUser(String email, UserProfileDtoRequest userProfileDtoRequest);

    /**
     * Get the rating of the user by user id.
     *
     * @param userId id of the user
     * @return {@link Double} rating of the user
     */
    Double findUserRating(Long userId);

    /**
     * Increase user rating by amount specified in {@link UserAddRatingDto}.
     *
     * @param userAddRatingDto contains rating data.
     */
    void increaseUserRating(UserAddRatingDto userAddRatingDto);

    /**
     * Increase user rating by amount specified in {@link UserAddRatingExternalDto}.
     *
     * @param userAddRatingDto contains rating data.
     */
    void increaseUserRating(UserAddRatingExternalDto userAddRatingDto);

    /**
     * Find list of {@link UserVO}'s by emails.
     *
     * @param emails user emails.
     * @return list of {@link UserVO}.
     */
    List<UserVO> findByEmails(List<String> emails);

    /**
     * Find list of user ids by emailPreference and periodicity.
     *
     * @param emailPreference of user.
     * @param periodicity     of notification.
     * @return list of {@link UserVO}.
     */
    List<UserVO> getUsersIdByEmailPreferenceAndEmailPeriodicity(EmailPreference emailPreference,
        EmailPreferencePeriodicity periodicity);

    /**
     * Get all user's friends ids by user id.
     *
     * @param userId id of the user.
     * @return list of friends ids.
     */
    List<Long> getAllUserFriendsIds(Long userId);

    /**
     * Get all user's friends ids by user email.
     *
     * @param email user's email.
     * @return list of friends ids.
     */
    List<Long> getAllUserFriendsIds(String email);

    /**
     * Get all user friends ids as a page.
     *
     * @param userId   id of the user.
     * @param pageable pageable configuration.
     * @return {@link Page}
     */
    PageableAdvancedDto<Long> getAllUserFriendsIds(Long userId, Pageable pageable);

    /**
     * Get all user friends ids as a page.
     *
     * @param email    email of the user.
     * @param pageable pageable configuration.
     * @return {@link Page}
     */
    PageableAdvancedDto<Long> getAllUserFriendsIds(String email, Pageable pageable);

    /**
     * Get top 6 friends ids with the highest rating.
     *
     * @param userId - {@link UserVO}'s id
     * @return {@link List} of friends ids
     */
    List<Long> getSixFriendsIdsWithTheHighestRating(Long userId);

    /**
     * Get top 6 friends ids with the highest rating.
     *
     * @param email - {@link UserVO}'s email
     * @return {@link List} of friends ids
     */
    List<Long> getSixFriendsIdsWithTheHighestRating(String email);

    /**
     * Method that allows to find {@link UserVOAdvancedDto} by id.
     *
     * @param id a value of {@link Long}
     * @return {@link UserVOAdvancedDto} with this id.
     */
    UserVOAdvancedDto findByIdAdvanced(Long id);

    /**
     * Method for getting user's social network url by social network name.
     *
     * @param socialNetworks    - {@link List} of user's {@link SocialNetworkVO}
     *                          instances.
     * @param socialNetworkName - name of {@link SocialNetworkVO}.
     */
    String getSocialNetworkUrlByName(List<SocialNetworkVO> socialNetworks, String socialNetworkName);

    /**
     * Method for creating a GreenCity user.
     *
     * @param createUserDto - {@link CreateGreenCityUserDto} that represents a new
     *                      user.
     * @return {@link Boolean} with the result of creation: true for success and
     *         false for failure
     */
    Boolean createUser(CreateGreenCityUserDto createUserDto);

    /**
     * Method for updating user's profilePicturePath.
     *
     * @param email              - user's email.
     * @param profilePicturePath - new picturePath.
     */
    void updateUserProfilePicture(String email, String profilePicturePath);

    /**
     * Method for updating user's name.
     *
     * @param userId   - {@link Long} of user's id.
     * @param userName - new user's name.
     */
    void updateUserName(Long userId, String userName);

    /**
     * Method for updating user's name.
     *
     * @param email    - user's email.
     * @param userName - new user's name.
     */
    void updateUserName(String email, String userName);

    /**
     * Method to find list of {@link GreenCityUserProfileDtoResponse} containing
     * information about user.
     *
     * @param userIds ids of users for whom to fetch the data
     * @return list of {@link GreenCityUserProfileDtoResponse} containing
     *         information about user
     */
    List<GreenCityUserProfileDtoResponse> findGreenCityUserProfilesByUserIds(List<Long> userIds);

    /**
     * Method to find list of {@link GreenCityUserProfileDtoResponse} containing
     * information about user.
     *
     * @param emails emails of users for whom to fetch the data
     * @return list of {@link GreenCityUserProfileDtoResponse} containing
     *         information about user
     */
    List<GreenCityUserProfileDtoResponse> findGreenCityUserProfilesByEmails(List<String> emails);

    /**
     * Method to set internal GreenCity ids for {@link UserVO}.
     *
     * @param users users for whom to set internal ids
     */
    void setInternalUserVOIds(List<UserVO> users);

    /**
     * Method to set internal GreenCity ids for {@link UserManagementDto}.
     *
     * @param users users for whom to set internal ids
     */
    void setInternalUserManagementDtoIds(List<UserManagementDto> users);

    /**
     * Method to set internal GreenCity ids for {@link UserManagementVO}.
     *
     * @param users users for whom to set internal ids
     */
    void setInternalUserManagementVOIds(List<UserManagementVO> users);
}