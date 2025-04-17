package greencity.client.config;

import feign.hystrix.FallbackFactory;
import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserLocationStatisticDto;
import greencity.dto.user.UserNotificationPreferenceVO;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.dto.user.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class UserRemoteClientFallbackFactory implements FallbackFactory<UserRemoteClient> {
    @Override
    public UserRemoteClient create(Throwable throwable) {
        return new UserRemoteClient() {
            @Override
            public Optional<UserVO> findNotDeactivatedByEmail(String email) {
                // log.error(ErrorMessage.USER_WITH_THIS_EMAIL_DOES_NOT_EXIST + "{}", email, throwable);
                return Optional.empty();
            }

            @Override
            public Optional<UserStatusDto> updateUserStatus(UserStatusDto userStatusDto) {
                //TODO: log
                return Optional.empty();
            }

            @Override
            public Optional<UserRoleDto> updateUserRole(Long id, Map<String, String> body) {
                //TODO: log
                return Optional.empty();
            }

            @Override
            public Optional<List<UserRoleStatisticDto>> getUserRolesDistribution() {
                //TODO: log
                return Optional.empty();
            }

            @Override
            public Optional<List<UserStatusStatisticDto>> getUserStatusesDistribution() {
                //TODO: log
                return Optional.empty();
            }

            @Override
            public Optional<List<UserLocationStatisticDto>> getUserLocationsDistribution(String groupBy) {
                //TODO: log
                return Optional.empty();
            }

            @Override
            public Optional<List<UserEmailPreferencesStatisticDto>> getUserEmailPreferencesDistribution() {
                //TODO: log
                return Optional.empty();
            }

            @Override
            public Optional<Long> countActiveUsers() {
                //TODO: log
                return Optional.empty();
            }

            @Override
            public List<UserNotificationPreferenceVO> findAllUserNotificationPreferencesByUserId(Long userId) {
                throw new RuntimeException("not implemented");
            }

            @Override
            public Boolean searchUserNotificationPreference(EmailPreferenceDto emailPreferenceDto) {
                throw new RuntimeException("not implemeneted");
            }
        };
    }
}
