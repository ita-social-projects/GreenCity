package greencity.security.jwt;

import greencity.dto.user.UserVO;
import greencity.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
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
import com.google.gson.JsonParseException;

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
        ReflectionTestUtils.setField(jwtTool, "accessTokenKey", "123123123123123123123123123123123123");
    }

    @Test
    void createAccessToken() {
        final String accessToken = jwtTool.createAccessToken(expectedEmail, expectedRole);
        System.out.println(accessToken);

        SecretKey key = Keys.hmacShaKeyFor(jwtTool.getAccessTokenKey().getBytes());

        String actualEmail = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(accessToken)
            .getPayload()
            .getSubject();
        assertEquals(expectedEmail, actualEmail);
        @SuppressWarnings({"unchecked, rawtype"})
        List<String> authorities = (List<String>) Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(accessToken)
            .getPayload()
            .get(ROLE);
        assertEquals(expectedRole, Role.valueOf(authorities.getFirst()));
    }

    @Test
    void createRefreshToken() {
        String s = "secret-refresh-token-key-bigger-key";
        UserVO userVO = new UserVO();
        userVO.setEmail(expectedEmail);
        userVO.setRole(expectedRole);
        userVO.setRefreshTokenKey(s);
        SecretKey key = Keys.hmacShaKeyFor(userVO.getRefreshTokenKey().getBytes());
        String refreshToken = jwtTool.createRefreshToken(userVO);
        String actualEmail = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(refreshToken)
            .getPayload()
            .getSubject();
        assertEquals(expectedEmail, actualEmail);
        @SuppressWarnings({"unchecked, rawtype"})
        List<String> authorities = (List<String>) Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(refreshToken)
            .getPayload()
            .get(ROLE);
        assertEquals(expectedRole, Role.valueOf(authorities.getFirst()));
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
    void getEmailOutOfAccessTokenThrowsJsonParseException() {
        assertThrows(JsonParseException.class,
            () -> jwtTool.getEmailOutOfAccessToken("YFicrqBFN0Q662HqkI2P8yuykgvJ.jiTgUqsUhN4ICHI"),
            "Error parsing JSON payload");
    }

    @Test
    void isTokenValidWithInvalidTokenTest() {
        String random = UUID.randomUUID().toString();
        assertFalse(jwtTool.isTokenValid(random, jwtTool.getAccessTokenKey()));
        boolean valid = jwtTool.isTokenValid("""
            eyJhbGciOiJIUzI1NiJ9\
            .eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUi\
            JdLCJpYXQiOjE1NzU4MzY5NjUsImV4cCI6MTU3NTgzNzg2NX0\
            .1kVcts6LCzUov-j0zMQqRXqIxeChUUv2gsw_zoLXtc8\
            """, jwtTool.getAccessTokenKey());
        assertFalse(valid);
    }

    @Test
    void isTokenValidWithValidTokenTest() {
        final String accessToken = """
            eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGhv\
            cml0aWVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE1NzU4NDUzNTAsImV4cCI6N\
            jE1NzU4NDUyOTB9.Lv0Rk8e1DhkONblzJecx4hCRpT1kKiQ1ns9mgZAWEq0\
            """;
        SecretKey key = Keys.hmacShaKeyFor(jwtTool.getAccessTokenKey().getBytes());
        Date expectedExpiration = new Date(61575845290000L); // 3921 year
        Date actualExpiration = Jwts.parser()
            .verifyWith(key).build()
            .parseSignedClaims(accessToken)
            .getPayload()
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
