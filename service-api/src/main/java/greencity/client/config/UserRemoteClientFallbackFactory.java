package greencity.client.config;

import feign.hystrix.FallbackFactory;
import greencity.client.UserRemoteClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserRemoteClientFallbackFactory implements FallbackFactory<UserRemoteClient> {
    @Override
    public UserRemoteClient create(Throwable throwable) {
        return new UserRemoteClient() {
        };
    }
}
