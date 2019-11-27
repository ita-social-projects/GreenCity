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
        ReflectionTestUtils.setField(jwtTool, "accessTokenKey", "123123");
    }

    @Test
    public void createAccessToken() {
        String accessToken =
            jwtTool.createAccessToken("nazar.stasyuk@gmail.com", ROLE.ROLE_USER);
        assertTrue(jwtTool.isTokenValid(accessToken, jwtTool.getAccessTokenKey()));
    }

    @Test
    public void isTokenValid() {
        String random = UUID.randomUUID().toString();
        assertFalse(jwtTool.isTokenValid(random, jwtTool.getAccessTokenKey()));
    }

    @Test
    public void createRefreshToken() {
        String s = UUID.randomUUID().toString();
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setRole(ROLE.ROLE_USER);
        user.setRefreshTokenKey(s);
        String token = jwtTool.createRefreshToken(user);
        assertNotNull(token);
    }
}
