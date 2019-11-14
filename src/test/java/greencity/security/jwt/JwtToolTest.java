package greencity.security.jwt;

import static org.junit.Assert.*;

import greencity.entity.enums.ROLE;
import greencity.service.UserService;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(value = MockitoJUnitRunner.class)
@SpringBootTest
public class JwtToolTest {

    @Mock
    private UserService userService;
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

    @Test
    public void getEmailByToken() {
        String email = "nazar.stasyuk@gmail.com";
        String accessToken = jwtTool.createAccessToken(email, ROLE.ROLE_USER);

        String emailByToken = jwtTool.getEmailByToken(accessToken);
        assertEquals(email, emailByToken);
    }
}
