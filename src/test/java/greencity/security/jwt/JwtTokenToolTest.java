package greencity.security.jwt;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.service.UserService;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(value = MockitoJUnitRunner.class)
@SpringBootTest
public class JwtTokenToolTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private JwtTokenTool jwtTokenTool;

    @Before
    public void init() {
        ReflectionTestUtils.setField(jwtTokenTool, "accessTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTokenTool, "refreshTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTokenTool, "tokenKey", "123123");
    }

    @Test
    public void createAccessToken() {
        String accessToken =
            jwtTokenTool.createAccessToken("nazar.stasyuk@gmail.com", ROLE.ROLE_USER);
        assertTrue(jwtTokenTool.isTokenValid(accessToken));
    }

    @Test
    public void createRefreshToken() {
        String accessToken = jwtTokenTool.createRefreshToken("nazar.stasyuk@gmail.com");
        assertTrue(jwtTokenTool.isTokenValid(accessToken));
    }

    @Test
    public void isTokenValid() {
        String random = UUID.randomUUID().toString();
        assertFalse(jwtTokenTool.isTokenValid(random));
    }

    @Test
    public void getEmailByToken() {
        String email = "nazar.stasyuk@gmail.com";
        String accessToken = jwtTokenTool.createAccessToken(email, ROLE.ROLE_USER);

        String emailByToken = jwtTokenTool.getEmailByToken(accessToken);
        assertEquals(email, emailByToken);
    }
}
