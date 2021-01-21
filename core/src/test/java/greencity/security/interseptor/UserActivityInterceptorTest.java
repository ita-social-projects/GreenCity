package greencity.security.interseptor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import greencity.client.RestClient;
import greencity.security.interceptor.UserActivityInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserActivityInterceptorTest {
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    Object handler;
    @Mock
    SecurityContext securityContext;
    @Mock
    RestClient restClient;

    @InjectMocks
    UserActivityInterceptor userActivityInterceptor;

    @Test
    void preHandleTest() throws Exception {
        String accessToken = "accessToken";
        Principal principal = mock(Principal.class);
        List<GrantedAuthority> authority = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new AnonymousAuthenticationToken("k", principal, authority);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal().toString()).thenReturn("test@mail.com");
        when(restClient.findIdByEmail("test@mail.com", accessToken)).thenReturn(1L);
        Date time = new Date();
        doNothing().when(restClient).updateUserLastActivityTime(1L, time, accessToken);
        assertTrue(userActivityInterceptor.preHandle(request, response, handler));
    }
}
