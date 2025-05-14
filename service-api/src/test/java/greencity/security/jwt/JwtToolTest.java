package greencity.security.jwt;

import static greencity.constant.AppConstant.ROLE;

import greencity.constant.AppConstant;
import greencity.dto.user.UserClaims;
import greencity.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import javax.crypto.SecretKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
@ExtendWith(SpringExtension.class)
class JwtToolTest {
    @Mock
    HttpServletRequest request;

    @InjectMocks
    private JwtTool jwtTool;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(jwtTool, "accessTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTool, "accessTokenKey", "123123123123123123123123123123123123");
    }

    @Test
    void createAccessToken() {
        String expectedEmail = "test@gmail.com";
        Role expectedRole = Role.ROLE_USER;
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
    void extractUserIdTest() {
        Long expectedResult = 5L;
        String jwt = Jwts.builder()
            .claim(AppConstant.JWT_USER_ID_CLAIM, expectedResult)
            .signWith(Keys.hmacShaKeyFor(jwtTool.getAccessTokenKey().getBytes()))
            .compact();

        Long actualResult = jwtTool.extractUserId(jwt);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void extractUserClaimsTest() {
        Long userId = 7L;
        String userEmail = "email@email.com";
        List<Role> roles = List.of(Role.ROLE_USER, Role.ROLE_UBS_EMPLOYEE);
        String jwt = Jwts.builder()
            .claim(AppConstant.JWT_USER_ID_CLAIM, userId)
            .claim(AppConstant.ROLE, roles)
            .subject(userEmail)
            .signWith(Keys.hmacShaKeyFor(jwtTool.getAccessTokenKey().getBytes()))
            .compact();

        UserClaims actualResult = jwtTool.extractUserClaims(jwt);

        assertEquals(userId, actualResult.userId());
        assertEquals(userEmail, actualResult.userEmail());
        assertEquals(roles, actualResult.roles());
    }

    @Test
    void getTokenFromHttpServletRequest() {
        final String expectedToken = "An AccessToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expectedToken);
        String actualToken = jwtTool.getTokenFromHttpServletRequest(request);
        assertEquals(expectedToken, actualToken);
    }
}
