package greencity.security.jwt;

import static greencity.constant.AppConstant.ROLE;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserClaims;
import greencity.enums.Role;
import greencity.exception.exceptions.NoJwtException;
import greencity.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Class that provides methods for working with JWT.
 *
 * @author Nazar Stasyuk && Yurii Koval.
 * @version 2.0
 */
@Slf4j
@Component
public class JwtTool {
    private final Integer accessTokenValidTimeInMinutes;
    private final String accessTokenKey;
    private final UserService userService;

    /**
     * Constructor.
     */
    public JwtTool(
        @Value("${accessTokenValidTimeInMinutes}") Integer accessTokenValidTimeInMinutes,
        @Value("${tokenKey}") String accessTokenKey,
        @Lazy UserService userService) {
        this.accessTokenValidTimeInMinutes = accessTokenValidTimeInMinutes;
        this.accessTokenKey = accessTokenKey;
        this.userService = userService;
    }

    /**
     * Method for creating access token.
     *
     * @param email this is email of user.
     * @param role  this is role of user.
     */
    public String createAccessToken(String email, Role role) {
        Set<String> roleNames = Collections.singleton(role.name());
        return createAccessToken(email, roleNames);
    }

    /**
     * Method for creating access token.
     *
     * @param email this is email of user.
     * @param roles this is list of roles of user.
     */
    public String createAccessToken(String email, List<Role> roles) {
        Set<String> roleNames = roles.stream().map(Role::name).collect(Collectors.toSet());
        return createAccessToken(email, roleNames);
    }

    /**
     * Method for creating access token.
     *
     * @param email     this is email of user.
     * @param roleNames this is list of role names of user.
     */
    private String createAccessToken(String email, Set<String> roleNames) {
        ClaimsBuilder claims = Jwts.claims().subject(email);
        claims.add(ROLE, roleNames);
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, accessTokenValidTimeInMinutes);
        return Jwts.builder()
            .claims(claims.build())
            .issuedAt(now)
            .expiration(calendar.getTime())
            .signWith(Keys.hmacShaKeyFor(
                accessTokenKey.getBytes(StandardCharsets.UTF_8)),
                Jwts.SIG.HS256)
            .compact();
    }

    /**
     * Returns access token key.
     *
     * @return accessTokenKey
     */
    public String getAccessTokenKey() {
        return accessTokenKey;
    }

    /**
     * Method that get token from {@link HttpServletRequest}.
     *
     * @param servletRequest this is your request.
     * @return {@link String} of token or null.
     */
    public String getTokenFromHttpServletRequest(HttpServletRequest servletRequest) {
        return Optional.ofNullable(servletRequest.getHeader("Authorization"))
            .filter(authHeader -> authHeader.startsWith("Bearer "))
            .map(token -> token.substring(7))
            .orElse(null);
    }

    /**
     * Method to extract jwt from a {@link NativeWebRequest} instance.
     *
     * @param nativeWebRequest request to extract jwt from
     * @return {@link String} jwt
     * @throws NoJwtException in case jwt could not be extracted from the request
     */
    public String extractJwtFromNativeWebRequest(NativeWebRequest nativeWebRequest) throws NoJwtException {
        String authorizationHeader = nativeWebRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(AppConstant.TOKEN_PREFIX)) {
            throw new NoJwtException(ErrorMessage.NO_JWT_TOKEN_FOUND);
        }
        return authorizationHeader.substring(AppConstant.TOKEN_PREFIX.length());
    }

    /**
     * Method to extract user claims as {@link UserClaims} from JWT.
     *
     * @param jwt {@link String} json web token
     * @return {@link UserClaims} extracted from token
     */
    public UserClaims extractUserClaims(String jwt) {
        Claims claims = extractClaims(jwt);

        return new UserClaims(
            extractUserId(claims),
            claims.getSubject(),
            extractUserRoles(claims));
    }

    private List<Role> extractUserRoles(Claims claims) {
        List<String> roleNames = (List<String>) claims.get(ROLE);
        return roleNames.stream().map(Role::valueOf).toList();
    }

    /**
     * Method to extract user id as claim from JWT.
     *
     * @param jwt {@link String} json web token
     * @return Long user id extracted from token
     */
    public Long extractUserId(String jwt) {
        return extractUserId(extractClaims(jwt));
    }

    private Long extractUserId(Claims claims) {
        String userEmail = claims.getSubject();
        return userService.findNotDeactivatedByEmail(userEmail).getId();
    }

    private Claims extractClaims(String jwt) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(accessTokenKey.getBytes()))
            .build()
            .parseSignedClaims(jwt)
            .getPayload();
    }
}
