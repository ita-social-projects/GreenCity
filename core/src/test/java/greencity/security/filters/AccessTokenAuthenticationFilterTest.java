package greencity.security.filters;

import greencity.security.jwt.JwtTool;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import greencity.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

@RunWith(PowerMockRunner.class)
public class AccessTokenAuthenticationFilterTest {
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain chain;
    @Mock
    JwtTool jwtTool;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    UserService userService;

    private AccessTokenAuthenticationFilter authenticationFilter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        authenticationFilter = new AccessTokenAuthenticationFilter(jwtTool, authenticationManager, userService);
    }

    @Test
    public void doFilterInternal() throws IOException, ServletException {
        when(jwtTool.getTokenFromHttpServletRequest(request)).thenReturn("SupeSecretAccessToken");
        when(authenticationManager.authenticate(any()))
            .thenReturn(new UsernamePasswordAuthenticationToken("Principal", null));
        doNothing().when(chain).doFilter(request, response);

        authenticationFilter.doFilterInternal(request, response, chain);
        verify(authenticationManager, times(1)).authenticate(any());
        verify(chain, times(1)).doFilter(any(), any());
    }
}