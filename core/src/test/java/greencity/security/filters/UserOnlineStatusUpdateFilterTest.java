package greencity.security.filters;

import greencity.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import static org.awaitility.Awaitility.await;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserOnlineStatusUpdateFilterTest {
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain chain;
    @Mock
    UserService userService;

    @InjectMocks
    private UserOnlineStatusUpdateFilter userOnlineStatusUpdateFilter;

    @Mock
    ThreadPoolTaskExecutor customThreadPool;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    @SneakyThrows
    void doFilterInternalTest() {
        String email = "test@gmail.com";

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        userOnlineStatusUpdateFilter.doFilterInternal(request, response, chain);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(
            () -> userService.updateUserLastActivityTimeByEmail(eq(email), any()));

        verify(chain).doFilter(request, response);
    }

    @Test
    @SneakyThrows
    void doFilterInternalTestWhenUserIsNotAuthorized() {
        userOnlineStatusUpdateFilter.doFilterInternal(request, response, chain);

        verify(userService, never()).updateUserLastActivityTimeByEmail(anyString(), any());
        verify(chain).doFilter(request, response);
    }
}
