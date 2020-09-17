package greencity.security.jwt;

import static greencity.constant.AppConstant.AUTHORITIES;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import io.jsonwebtoken.Jwts;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Yurii Koval
 */
@ExtendWith(SpringExtension.class)public class JwtToolTest {
    private final String expectedEmail = "test@gmail.com";
    private final ROLE expectedRole = ROLE.ROLE_USER;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    private JwtTool jwtTool;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(jwtTool, "accessTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTool, "refreshTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTool, "accessTokenKey", "123123123");
    }

    @Test
    public void createAccessToken() {
        final String accessToken = jwtTool.createAccessToken(expectedEmail, expectedRole);
        System.out.println(accessToken);
        String actualEmail =  Jwts.parser()
            .setSigningKey(jwtTool.getAccessTokenKey())
            .parseClaimsJws(accessToken)
            .getBody()
            .getSubject();
        assertEquals(expectedEmail, actualEmail);
        @SuppressWarnings({"unchecked, rawtype"})
        List<String> authorities = (List<String>) Jwts.parser()
            .setSigningKey(jwtTool.getAccessTokenKey())
            .parseClaimsJws(accessToken)
            .getBody()
            .get(AUTHORITIES);
        assertEquals(expectedRole, ROLE.valueOf(authorities.get(0)));
    }

    @Test
    public void createRefreshToken() {
        String s = "secret-refresh-token-key";
        User user = new User();
        user.setEmail(expectedEmail);
        user.setRole(expectedRole);
        user.setRefreshTokenKey(s);
        String refreshToken = jwtTool.createRefreshToken(user);
        String actualEmail =  Jwts.parser()
            .setSigningKey(user.getRefreshTokenKey())
            .parseClaimsJws(refreshToken)
            .getBody()
            .getSubject();
        assertEquals(expectedEmail, actualEmail);
        @SuppressWarnings({"unchecked, rawtype"})
        List<String> authorities = (List<String>) Jwts.parser()
            .setSigningKey(user.getRefreshTokenKey())
            .parseClaimsJws(refreshToken)
            .getBody()
            .get(AUTHORITIES);
        assertEquals(expectedRole, ROLE.valueOf(authorities.get(0)));
    }

    @Test
    public void getEmailOutOfAccessToken() {
        String actualEmail = jwtTool.getEmailOutOfAccessToken("eyJhbGciOiJIUzI1NiJ9"
            + ".eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGh"
            + "vcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE1NzU4MzY5NjUsImV4cCI6OTk5OTk5OTk5OTk5fQ"
            + ".YFicrqBFN0Q662HqkI2P8yuykgvJjiTgUqsUhN4ICHI");
        assertEquals(expectedEmail, actualEmail);
    }

    @Test
    public void isTokenValid() {
        String random = UUID.randomUUID().toString();
        assertFalse(jwtTool.isTokenValid(random, jwtTool.getAccessTokenKey()));
        boolean valid = jwtTool.isTokenValid("eyJhbGciOiJIUzI1NiJ9"
            + ".eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUi"
            + "JdLCJpYXQiOjE1NzU4MzY5NjUsImV4cCI6MTU3NTgzNzg2NX0"
            + ".1kVcts6LCzUov-j0zMQqRXqIxeChUUv2gsw_zoLXtc8", jwtTool.getAccessTokenKey());
        assertFalse(valid);
    }

    @Test
    public void getTokenFromHttpServletRequest() {
        final String expectedToken = "An AccessToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expectedToken);
        String actualToken = jwtTool.getTokenFromHttpServletRequest(request);
        assertEquals(expectedToken, actualToken);
    }
}
