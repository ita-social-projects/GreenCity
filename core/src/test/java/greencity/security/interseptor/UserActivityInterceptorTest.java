package greencity.security.interseptor;

import greencity.security.interceptor.UserActivityInterceptor;
import greencity.service.UserService;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    UserService userService;

    @InjectMocks
    UserActivityInterceptor userActivityInterceptor;

    @Test
    @Disabled
    void preHandleTest() throws Exception {
        Principal principal = mock(Principal.class);
        List<GrantedAuthority> authority = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new AnonymousAuthenticationToken("k", principal, authority);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal().toString()).thenReturn("test@mail.com");
        when(userService.findIdByEmail("test@mail.com")).thenReturn(1L);
        Date time = new Date();
        doNothing().when(userService).updateUserLastActivityTime(1L, time);
        userActivityInterceptor.preHandle(request, response, handler);
        verify(userService).updateUserLastActivityTime(1L, time);
    }
}
