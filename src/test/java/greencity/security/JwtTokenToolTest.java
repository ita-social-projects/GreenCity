package greencity.security;

import java.util.UUID;

import greencity.entity.enums.ROLE;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

@RunWith(value = MockitoJUnitRunner.class)
@SpringBootTest
public class JwtTokenToolTest {

    @InjectMocks private JwtTokenTool jwtTokenTool;

    @Before
    public void init() {
        ReflectionTestUtils.setField(jwtTokenTool, "accessTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTokenTool, "refreshTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTokenTool, "tokenKey", "123123");
    }

    @Test
    public void createAccessToken() {
        String accessToken =
                jwtTokenTool.createAccessToken("nazar.stasyuk@gmail.com", ROLE.USER_ROLE);
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
        String accessToken = jwtTokenTool.createAccessToken(email, ROLE.USER_ROLE);

        String emailByToken = jwtTokenTool.getEmailByToken(accessToken);
        assertEquals(email, emailByToken);
    }
}
