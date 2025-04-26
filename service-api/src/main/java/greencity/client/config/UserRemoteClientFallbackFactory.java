package greencity.client.config;

import feign.hystrix.FallbackFactory;
import greencity.client.UserRemoteClient;
import greencity.dto.emailpreference.EmailPreferenceDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
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
                // log.error(ErrorMessage.USER_WITH_THIS_EMAIL_DOES_NOT_EXIST + "{}", email,
                // throwable);
                return Optional.empty();
            }

            @Override
            public Optional<UserVO> findNotDeactivatedById(Long id) {
                throw new RuntimeException("not implemented");
            }

            @Override
            public Optional<UserStatusDto> updateUserStatus(UserStatusDto userStatusDto) {
                // TODO: log
                return Optional.empty();
            }

            @Override
            public Optional<UserRoleDto> updateUserRole(Long id, Map<String, String> body) {
                // TODO: log
                return Optional.empty();
            }

            @Override
            public List<UserRoleStatisticDto> getUserRolesDistribution() {
                throw new RuntimeException("not implemented");
            }

            @Override
            public List<UserStatusStatisticDto> getUserStatusesDistribution() {
                throw new RuntimeException("not implemented");
            }

            @Override
            public List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution() {
                throw new RuntimeException("not implemented");
            }

            @Override
            public Long countActiveUsers() {
                throw new RuntimeException("not implemented");
            }

            @Override
            public List<UserNotificationPreferenceVO> findAllUserNotificationPreferencesByUserId(Long userId) {
                throw new RuntimeException("not implemented");
            }

            @Override
            public Boolean searchUserNotificationPreference(EmailPreferenceDto emailPreferenceDto) {
                throw new RuntimeException("not implemeneted");
            }

            @Override
            public Boolean checkIfTheUserIsOnline(Long userId) {
                throw new RuntimeException("not implemeneted");
            }

            @Override
            public List<UserVO> findAllByEmailPreferenceAndEmailPeriodicity(String emailPreference,
                String periodicity) {
                throw new RuntimeException("not implemeneted");
            }

            @Override
            public List<UserRegistrationStatisticDto> getUserRegistrationsByDateRange(LocalDateTime startDate,
                LocalDateTime endDate, DateGranularity granularity) {
                throw new RuntimeException("not implemeneted");
            }

            @Override
            public List<Long> getActivatedUsersIds(List<Long> ids) {
                throw new RuntimeException("not implemeneted");
            }

            @Override
            public Optional<UserVOAdvancedDto> findNotDeactivatedByEmailAdvanced(String email) {
                throw new RuntimeException("not implemented");
            }
        };
    }
}
