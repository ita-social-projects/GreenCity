package greencity.security.filters;

import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccessTokenAuthenticationFilterTest {
    private PrintStream systemOut;
    private ByteArrayOutputStream systemOutContent;

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

    @InjectMocks
    private AccessTokenAuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        systemOut = System.out;
        systemOutContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutContent));
    }

    @AfterEach
    void restoreSystemOutStream() {
        System.setOut(systemOut);
    }

    @Test
    void doFilterInternalTest() throws IOException, ServletException {
        when(jwtTool.getTokenFromHttpServletRequest(request)).thenReturn("SuperSecretAccessToken");
        when(authenticationManager.authenticate(any()))
            .thenReturn(new UsernamePasswordAuthenticationToken("test@mail.com", null));
        when(userService.findNotDeactivatedByEmail("test@mail.com"))
            .thenReturn(Optional.of(UserVO.builder().id(1L).build()));
        doNothing().when(chain).doFilter(request, response);

        authenticationFilter.doFilterInternal(request, response, chain);
        verify(authenticationManager).authenticate(any());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternalTokenHasExpiredTest() throws IOException, ServletException {
        String token = "SuperSecretAccessToken";
        when(jwtTool.getTokenFromHttpServletRequest(request)).thenReturn(token);
        when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(token, null)))
            .thenThrow(ExpiredJwtException.class);
        authenticationFilter.doFilterInternal(request, response, chain);
        assertTrue(systemOutContent.toString().contains("Token has expired: "));
    }

    @Test
    void doFilterInternalAccessDeniedTest() throws IOException, ServletException {
        String token = "SuperSecretAccessToken";
        when(jwtTool.getTokenFromHttpServletRequest(request)).thenReturn(token);
        when(authenticationManager.authenticate(any()))
            .thenReturn(new UsernamePasswordAuthenticationToken("test@mail.com", null));
        when(userService.findNotDeactivatedByEmail("test@mail.com")).thenThrow(RuntimeException.class);
        authenticationFilter.doFilterInternal(request, response, chain);
        assertTrue(systemOutContent.toString().contains("Access denied with token: "));
    }
}
