package greencity.security.filters;

import greencity.ModelUtils;
import greencity.entity.User;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        Optional<User> user = Optional.of(ModelUtils.getUser());
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(jwtTool.getTokenFromHttpServletRequest(request)).thenReturn("SupeSecretAccessToken");
        when(jwtTool.getEmailOutOfAccessToken("SupeSecretAccessToken")).thenReturn("testmail@gmail.com");
        when(userService.findNotDeactivatedByEmail("testmail@gmail.com")).thenReturn(user);
        when(authenticationManager.authenticate(any()))
            .thenReturn(new UsernamePasswordAuthenticationToken("Principal", null, authorities));
        doNothing().when(chain).doFilter(request, response);

        authenticationFilter.doFilterInternal(request, response, chain);
        verify(authenticationManager).authenticate(any());
        verify(chain).doFilter(any(), any());
    }
}