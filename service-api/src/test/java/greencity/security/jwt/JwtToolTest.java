package greencity.security.jwt;

import static greencity.constant.AppConstant.ROLE;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserClaims;
import greencity.dto.user.UserVO;
import greencity.enums.Role;
import greencity.exception.exceptions.NoJwtException;
import greencity.properties.SecurityProperties;
import greencity.service.UserService;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * @author Yurii Koval
 */
@ExtendWith(SpringExtension.class)
class JwtToolTest {
    @Mock
    HttpServletRequest request;
    @Mock
    UserService userService;
    @Mock
    SecurityProperties securityProperties;

    @InjectMocks
    private JwtTool jwtTool;

    @BeforeEach
    void init() {
        when(securityProperties.getJwtAccessTokenExpiration()).thenReturn(15);
        when(securityProperties.getAccessTokenKey()).thenReturn("123123123123123123123123123123123123");
    }

    @Test
    void createAccessToken() {
        String expectedEmail = "test@gmail.com";
        Role expectedRole = Role.ROLE_USER;
        final String accessToken = jwtTool.createAccessToken(expectedEmail, expectedRole);

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
    void createAccessTokenMultipleRolesTest() {
        String expectedEmail = "test@gmail.com";
        List<Role> expectedRoles = List.of(Role.ROLE_USER, Role.ROLE_ADMIN);
        String accessToken = jwtTool.createAccessToken(expectedEmail, expectedRoles);

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
        List<Role> actualRoles = authorities.stream().map(Role::valueOf).toList();
        assertEquals(expectedRoles, actualRoles);
    }

    @Test
    void extractJwtFromNativeWebRequestTest() {
        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        String token = "token";
        String authHeader = "Bearer " + token;

        when(nativeWebRequest.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn(authHeader);

        String actualResult = jwtTool.extractJwtFromNativeWebRequest(nativeWebRequest);

        assertEquals(token, actualResult);
    }

    @Test
    void extractJwtFromNativeWebRequestWhenNoJwtTest() {
        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        String authHeader = "Invalid bearer token";
        String expectedExceptionMessage = ErrorMessage.NO_JWT_TOKEN_FOUND;

        when(nativeWebRequest.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn(authHeader);

        var ex = assertThrows(
            NoJwtException.class,
            () -> jwtTool.extractJwtFromNativeWebRequest(nativeWebRequest));
        assertEquals(expectedExceptionMessage, ex.getMessage());
    }

    @Test
    void extractJwtFromNativeWebRequestWhenAuthHeaderNullTest() {
        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        String authHeader = null;
        String expectedExceptionMessage = ErrorMessage.NO_JWT_TOKEN_FOUND;

        when(nativeWebRequest.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn(authHeader);

        var ex = assertThrows(
            NoJwtException.class,
            () -> jwtTool.extractJwtFromNativeWebRequest(nativeWebRequest));
        assertEquals(expectedExceptionMessage, ex.getMessage());
    }

    @Test
    void extractUserIdTest() {
        UserVO user = ModelUtils.getUserVO();
        Long id = user.getId();
        String email = user.getEmail();
        String jwt = Jwts.builder()
            .subject(email)
            .signWith(Keys.hmacShaKeyFor(jwtTool.getAccessTokenKey().getBytes()))
            .compact();

        when(userService.findNotDeactivatedByEmail(email)).thenReturn(user);

        Long actualResult = jwtTool.extractUserId(jwt);

        assertEquals(id, actualResult);
    }

    @Test
    void extractUserClaimsTest() {
        UserVO user = ModelUtils.getUserVO();
        String email = user.getEmail();
        List<Role> roles = List.of(Role.ROLE_USER, Role.ROLE_UBS_EMPLOYEE);
        String jwt = Jwts.builder()
            .claim("userId", 999L)
            .claim(AppConstant.ROLE, roles)
            .subject(email)
            .signWith(Keys.hmacShaKeyFor(jwtTool.getAccessTokenKey().getBytes()))
            .compact();

        when(userService.findNotDeactivatedByEmail(email)).thenReturn(user);

        UserClaims actualResult = jwtTool.extractUserClaims(jwt);

        assertEquals(user.getId(), actualResult.userId());
        assertEquals(email, actualResult.userEmail());
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
