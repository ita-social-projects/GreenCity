package greencity.client;

import greencity.client.config.UserRemoteClientFallbackFactory;
import greencity.client.config.UserRemoteClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "user-remote-client",
        url = "${greencityuser.server.address}",
        configuration = UserRemoteClientInterceptor.class,
        fallbackFactory = UserRemoteClientFallbackFactory.class
)
public interface UserRemoteClient {
}
