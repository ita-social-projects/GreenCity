package greencity.client;

import greencity.client.config.UserRemoteClientFallbackFactory;
import greencity.client.config.UserRemoteClientInterceptor;
import greencity.dto.PageableDto;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserNotificationPreferenceVO;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.enums.DateGranularity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@FeignClient(
    name = "user-remote-client",
    url = "${greencityuser.server.address}",
    configuration = UserRemoteClientInterceptor.class,
    fallbackFactory = UserRemoteClientFallbackFactory.class)
@Component
public interface UserRemoteClient {
    String EMAIL = "email";

    /**
     * Finds {@link UserVO} that is not 'DEACTIVATED' by {@link UserVO}'s Email.
     *
     * @param email {@link UserVO}'s Email.
     * @return {@link Optional} of {@link UserVO}.
     */
    @GetMapping("/user/findNotDeactivatedByEmail")
    Optional<UserVO> findNotDeactivatedByEmail(@RequestParam(EMAIL) String email);

    /**
     * Method that allow you to find not 'DEACTIVATED' {@link UserVO} by id.
     *
     * @param id - {@link UserVO}'s id
     * @return {@link Optional} of found {@link UserVO}.
     */
    @GetMapping("/user/findNotDeactivatedById")
    Optional<UserVO> findNotDeactivatedById(@RequestParam Long id);

    @PatchMapping("/user/status")
    Optional<UserStatusDto> updateUserStatus(@RequestBody UserStatusDto userStatusDto);

    @PatchMapping("/user/{id}/role")
    Optional<UserRoleDto> updateUserRole(
        @PathVariable Long id,
        @RequestBody Map<String, String> body);

    @GetMapping("/user/roles-distribution")
    List<UserRoleStatisticDto> getUserRolesDistribution();

    @GetMapping("/user/statuses-distribution")
    List<UserStatusStatisticDto> getUserStatusesDistribution();

    @GetMapping("/user/email-preferences-distribution")
    List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution();

    @GetMapping("/user/count-active-users")
    Long countActiveUsers();

    /**
     * Get user notification preferences by user id.
     *
     * @return list of {@link UserNotificationPreferenceVO}
     */
    @GetMapping("/user-notification-preference")
    List<UserNotificationPreferenceVO> findAllUserNotificationPreferencesByUserId(@RequestParam Long userId);

    /**
     * Check is user notification preference exists by params in EmailPreferenceDto.
     *
     * @return boolean of whether UserNotificationPreference exists
     */
    @GetMapping("/user-notification-preference/search")
    Boolean searchUserNotificationPreference(@RequestBody EmailPreferenceDto emailPreferenceDto);

    /**
     * The method checks by id if a {@link UserVO} is online.
     *
     * @param userId id of the user
     * @return boolean of whether user by that id is online.
     */
    @GetMapping("/user/isOnline/{userId}/")
    Boolean checkIfTheUserIsOnline(@PathVariable Long userId);

    /**
     * Find users by email preference and email periodicity.
     *
     * @param emailPreference user's email preference.
     * @param periodicity     email periodicity.
     * @return list of {@link UserVO}
     */
    @GetMapping("/user/email")
    List<UserVO> findAllByEmailPreferenceAndEmailPeriodicity(
        @RequestParam("email-preference") String emailPreference,
        @RequestParam("email-periodicity") String periodicity);

    /**
     * Method to get list of dates and counts of registered users.
     *
     * @param startDate   {@code LocalDateTime} startDate.
     * @param endDate     {@code LocalDateTime} endDate.
     * @param granularity {@link DateGranularity} (eg. day, week, month, year).
     * @return {@link List} of {@link UserRegistrationStatisticDto}.
     */
    @GetMapping("/user/registration-statistics")
    List<UserRegistrationStatisticDto> getUserRegistrationsByDateRange(
        @RequestParam("start-date") LocalDateTime startDate,
        @RequestParam("end-date") LocalDateTime endDate,
        @RequestParam("granularity") DateGranularity granularity);

    /**
     * Retrieves the list of IDs of users who have the user status set to
     * {@code ACTIVATED}.
     *
     * @param ids a list of user IDs to check; may be {@code null} or empty to
     *            indicate all users
     * @return a list of {@code Long} values representing the IDs of all activated
     *         users
     */
    @GetMapping("/user/activated-ids")
    List<Long> getActivatedUsersIds(@RequestParam(value = "ids", required = false) List<Long> ids);

    /**
     * Finds {@link UserVOAdvancedDto} that is not 'DEACTIVATED' by
     * {@link UserVOAdvancedDto}'s id.
     *
     * @param id {@link UserVOAdvancedDto}'s id.
     * @return {@link Optional} of {@link UserVOAdvancedDto}.
     */
    @GetMapping("/user/findNotDeactivatedById")
    Optional<UserVOAdvancedDto> findNotDeactivatedByIdAdvanced(@RequestParam Long id);

    /**
     * Method that returns page with all {@link SocialNetworkImageResponseDTO}.
     *
     * @param pageable {@link Pageable}.
     * @return {@link PageableDto} of {@link SocialNetworkImageResponseDTO}.
     */
    @GetMapping("/management/socialnetworkimages/get-all-remote")
    PageableDto<SocialNetworkImageResponseDTO> getAllSocialNetworkImagesRemote(Pageable pageable);

//    /**
//     * Method for creating SocialNetworkImageVO.
//     *
//     * @param socialNetworkImageRequestDTO dto for {@link SocialNetworkImageVO}
//     *                                     entity.
//     * @param file                         of {@link MultipartFile}
//     */
//    @PostMapping(
//            value = "/management/socialnetworkimages/save-remote",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    void saveSocialImageRemote(@Valid @RequestPart("socialNetworkImageRequestDTO") SocialNetworkImageRequestDTO socialNetworkImageRequestDTO,
//                               @RequestPart(required = false, name = "file") MultipartFile file);

    /**
     * Method which deletes SocialNetworkImageVO by given id.
     *
     * @param id of Social Network Images
     * @return {@link Long} id og the deleted image
     */
    @DeleteMapping("/management/socialnetworkimages/delete")
    Long deleteSocialImage(@RequestParam("id") Long id);

    /**
     * Method for deleting SocialNetworkImageVO by given id.
     *
     * @param listId list of IDs.
     * @return {@link List} of the deleted image ids
     */
    @DeleteMapping("/management/socialnetworkimages/deleteAll")
    List<Long> deleteAllImages(@RequestBody List<Long> listId);

    /**
     * Method for getting socialnetworkimages by id.
     *
     * @param id of Eco New
     * @return {@link SocialNetworkImageResponseDTO} instance.
     */
    @GetMapping("/management/socialnetworkimages/find")
    SocialNetworkImageResponseDTO getEcoNewsById(@RequestParam("id") Long id);

//    /**
//     * Method which updates SocialNetworkImage.
//     *
//     * @param socialNetworkImageResponseDTO of
//     *                                      {@link SocialNetworkImageResponseDTO}.
//     * @param file                          of {@link MultipartFile}.
//     */
//    @PutMapping("/management/socialnetworkimages/")
//    void updateSocialImage(@Valid @RequestPart SocialNetworkImageResponseDTO socialNetworkImageResponseDTO,
//                                     @RequestPart(required = false, name = "file") MultipartFile file);
}
