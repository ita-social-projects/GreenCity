package greencity.client.config;

import feign.hystrix.FallbackFactory;
import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        };
    }
}
