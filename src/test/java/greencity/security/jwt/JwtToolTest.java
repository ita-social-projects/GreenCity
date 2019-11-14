package greencity.security.jwt;

import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(value = MockitoJUnitRunner.class)
@SpringBootTest
public class JwtToolTest {

    @Mock
    private UserService userService;

    @Mock
    private Function<String, String> tokenToEmailParser;

    @InjectMocks
    private JwtTool jwtTool;

    @Before
    public void init() {
        ReflectionTestUtils.setField(jwtTool, "accessTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTool, "refreshTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTool, "tokenKey", "123123");
    }

    @Test
    public void createAccessToken() {
        String accessToken =
            jwtTool.createAccessToken("nazar.stasyuk@gmail.com", ROLE.ROLE_USER);
        assertTrue(jwtTool.isTokenValid(accessToken));
    }

    @Test
    public void createRefreshToken() {
        String accessToken = jwtTool.createRefreshToken("nazar.stasyuk@gmail.com");
        assertTrue(jwtTool.isTokenValid(accessToken));
    }

    @Test
    public void isTokenValid() {
        String random = UUID.randomUUID().toString();
        assertFalse(jwtTool.isTokenValid(random));
    }

    /**
     * Tests getAuthentication method.
     * When given token is not valid OR its user is deactivated.
     */
    @Test
    public void getAuthenticationRefuse() {
        when(tokenToEmailParser.apply(anyString())).thenReturn("test.email@gmail.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        assertNull(jwtTool.getAuthentication("a token"));

        User blockedUser = new User();
        blockedUser.setUserStatus(UserStatus.DEACTIVATED);
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(blockedUser));
        assertNull(jwtTool.getAuthentication("a token"));

        Mockito.verify(userService, times(2)).findByEmail(anyString());
        Mockito.verify(userService, never()).updateLastVisit(any());
    }

    /**
     * Tests getAuthentication method.
     * When
     */
    @Test
    public void getAuthenticationOfUser() {
        when(tokenToEmailParser.apply(anyString())).thenReturn("test.email@gmail.com");

        User activatedUser = new User();
        activatedUser.setUserStatus(UserStatus.ACTIVATED);
        activatedUser.setEmail("test.email@gmail.com");
        activatedUser.setRole(ROLE.ROLE_USER);
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(activatedUser));
        when(userService.updateLastVisit(any())).thenReturn(activatedUser);
        assertNotNull(jwtTool.getAuthentication("a token"));

        new UsernamePasswordAuthenticationToken(
                activatedUser.getEmail(),
                "",
                Collections.singleton(new SimpleGrantedAuthority(activatedUser.getRole().name())));
        assertEquals(0, 0);

        Mockito.verify(userService, times(1)).findByEmail(anyString());
        Mockito.verify(userService, times(1)).updateLastVisit(any());
    }
}
