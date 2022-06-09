package greencity.security.jwt;

import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.Role;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static greencity.constant.AppConstant.ROLE;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Yurii Koval
 */
@ExtendWith(SpringExtension.class)
class JwtToolTest {
    private final String expectedEmail = "test@gmail.com";
    private final Role expectedRole = Role.ROLE_USER;

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
    void createAccessToken() {
        final String accessToken = jwtTool.createAccessToken(expectedEmail, expectedRole);
        System.out.println(accessToken);
        String actualEmail = Jwts.parser()
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
            .get(ROLE);
        assertEquals(expectedRole, Role.valueOf(authorities.get(0)));
    }

    @Test
    void createRefreshToken() {
        String s = "secret-refresh-token-key";
        User userf = new User();
        UserVO userVO = new UserVO();
        userVO.setEmail(expectedEmail);
        userVO.setRole(expectedRole);
        userVO.setRefreshTokenKey(s);
        String refreshToken = jwtTool.createRefreshToken(userVO);
        String actualEmail = Jwts.parser()
            .setSigningKey(userVO.getRefreshTokenKey())
            .parseClaimsJws(refreshToken)
            .getBody()
            .getSubject();
        assertEquals(expectedEmail, actualEmail);
        @SuppressWarnings({"unchecked, rawtype"})
        List<String> authorities = (List<String>) Jwts.parser()
            .setSigningKey(userVO.getRefreshTokenKey())
            .parseClaimsJws(refreshToken)
            .getBody()
            .get(ROLE);
        assertEquals(expectedRole, Role.valueOf(authorities.get(0)));
    }

    @Test
    void getEmailOutOfAccessToken() {
        String actualEmail = jwtTool.getEmailOutOfAccessToken("eyJhbGciOiJIUzI1NiJ9"
            + ".eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGh"
            + "vcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE1NzU4MzY5NjUsImV4cCI6OTk5OTk5OTk5OTk5fQ"
            + ".YFicrqBFN0Q662HqkI2P8yuykgvJjiTgUqsUhN4ICHI");
        assertEquals(expectedEmail, actualEmail);
    }

    @Test
    void isTokenValidWithInvalidTokenTest() {
        String random = UUID.randomUUID().toString();
        assertFalse(jwtTool.isTokenValid(random, jwtTool.getAccessTokenKey()));
        boolean valid = jwtTool.isTokenValid("eyJhbGciOiJIUzI1NiJ9"
            + ".eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUi"
            + "JdLCJpYXQiOjE1NzU4MzY5NjUsImV4cCI6MTU3NTgzNzg2NX0"
            + ".1kVcts6LCzUov-j0zMQqRXqIxeChUUv2gsw_zoLXtc8", jwtTool.getAccessTokenKey());
        assertFalse(valid);
    }

    @Test
    void isTokenValidWithValidTokenTest() {
        final String accessToken = "eyJhbGciOiJIUzI1NiJ9"
            + ".eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVN"
            + "FUiJdLCJpYXQiOjE1NzU4NDUzNTAsImV4cCI6NjE1NzU4NDUyOTB9"
            + ".x1D799yGc0dj2uWDQYusnLyG5r6-Rjj6UgBhp2JjVDE";
        Date expectedExpiration = new Date(61575845290000L); // 3921 year
        Date actualExpiration = Jwts.parser()
            .setSigningKey(jwtTool.getAccessTokenKey())
            .parseClaimsJws(accessToken)
            .getBody()
            .getExpiration();
        jwtTool.isTokenValid(accessToken, jwtTool.getAccessTokenKey());
        assertEquals(expectedExpiration, actualExpiration);
    }

    @Test
    void getTokenFromHttpServletRequest() {
        final String expectedToken = "An AccessToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expectedToken);
        String actualToken = jwtTool.getTokenFromHttpServletRequest(request);
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void generateTokenKeyTest() {
        assertNotNull(jwtTool.generateTokenKey());
    }
}
