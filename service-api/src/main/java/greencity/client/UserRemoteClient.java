package greencity.client;

import greencity.client.config.UserRemoteClientFallbackFactory;
import greencity.client.config.UserRemoteClientInterceptor;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserNotificationPreferenceVO;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.dto.user.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
     * Method returns six user friends sorted by rating.
     *
     * @return list of {@link UserVO}.
     */
    @GetMapping("/user/{userId}/sixFriends/")
    List<UserVO> getSixFriendsWithTheHighestRating(@PathVariable Long userId);

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
            @RequestParam("email-periodicity") String periodicity
    );
}
