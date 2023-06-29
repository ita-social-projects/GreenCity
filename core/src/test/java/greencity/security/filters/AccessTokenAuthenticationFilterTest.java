package greencity.security.filters;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
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

import static greencity.TestConst.EMAIL;
import static greencity.TestConst.TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccessTokenAuthenticationFilterTest {
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
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    public void setUp() {
        listAppender = new ListAppender<>();
        listAppender.start();

        logger = (Logger) org.slf4j.LoggerFactory.getLogger(AccessTokenAuthenticationFilter.class);
        logger.addAppender(listAppender);
        logger.setLevel(Level.INFO);
    }

    @AfterEach
    public void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void doFilterInternalTest() throws IOException, ServletException {
        when(jwtTool.getTokenFromHttpServletRequest(request)).thenReturn(TOKEN);
        when(authenticationManager.authenticate(any()))
            .thenReturn(new UsernamePasswordAuthenticationToken(EMAIL, null));
        when(userService.isNotDeactivatedByEmail(EMAIL)).thenReturn(true);
        doNothing().when(chain).doFilter(request, response);

        authenticationFilter.doFilterInternal(request, response, chain);
        verify(authenticationManager).authenticate(any());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternalTokenHasExpiredTest() throws IOException, ServletException {
        // given
        when(jwtTool.getTokenFromHttpServletRequest(request)).thenReturn(TOKEN);
        when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(TOKEN, null)))
                .thenThrow(ExpiredJwtException.class);

        // when
        authenticationFilter.doFilterInternal(request, response, chain);

        // then
        assertEquals("Token has expired: " + TOKEN, listAppender.list.get(0).getMessage());
        listAppender.list.clear();
    }

    @Test
    void doFilterInternalAccessDeniedTest() throws IOException, ServletException {
        // given
        when(jwtTool.getTokenFromHttpServletRequest(request)).thenReturn(TOKEN);
        when(authenticationManager.authenticate(any()))
            .thenReturn(new UsernamePasswordAuthenticationToken(EMAIL, null));
        when(userService.isNotDeactivatedByEmail(EMAIL)).thenThrow(RuntimeException.class);

        // when
        authenticationFilter.doFilterInternal(request, response, chain);

        // then
        assertEquals("Access denied with token: " + TOKEN, listAppender.list.get(0).getMessage());
        listAppender.list.clear();
    }
}
