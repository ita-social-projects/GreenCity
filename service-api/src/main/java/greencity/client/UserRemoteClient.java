package greencity.client;

import greencity.client.config.UserRemoteClientFallbackFactory;
import greencity.client.config.UserRemoteClientInterceptor;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserLocationStatisticDto;
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
        fallbackFactory = UserRemoteClientFallbackFactory.class
)
@Component
public interface UserRemoteClient {

    String EMAIL = "email";
    String GROUP_BY = "group-by";

    /**
     * Finds {@link UserVO} that is not 'DEACTIVATED' by {@link UserVO}'s Email.
     *
     * @param email {@link UserVO}'s Email.
     * @return {@link Optional} of {@link UserVO}.
     */
    @GetMapping("/user/findNotDeactivatedByEmail")
    Optional<UserVO> findNotDeactivatedByEmail(@RequestParam(EMAIL) String email);

    @PatchMapping("/user/status")
    Optional<UserStatusDto> updateUserStatus(@RequestBody UserStatusDto userStatusDto);

    @PatchMapping("/user/{id}/role")
    Optional<UserRoleDto> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    );

    @GetMapping("/user/roles-distribution")
    Optional<List<UserRoleStatisticDto>> getUserRolesDistribution();

    @GetMapping("/user/statuses-distribution")
    Optional<List<UserStatusStatisticDto>> getUserStatusesDistribution();

    @GetMapping("/user/locations-distribution")
    Optional<List<UserLocationStatisticDto>> getUserLocationsDistribution(@RequestParam(GROUP_BY) String groupBy);

    @GetMapping("/user/email-preferences-distribution")
    Optional<List<UserEmailPreferencesStatisticDto>> getUserEmailPreferencesDistribution();

    @GetMapping("/user/count-active-users")
    Optional<Long> countActiveUsers();
}
