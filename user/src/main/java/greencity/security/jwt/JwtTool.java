package greencity.security.jwt;

import greencity.dto.user.UserVO;
import greencity.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static greencity.constant.AppConstant.AUTHORITIES;

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
    private final Integer refreshTokenValidTimeInMinutes;
    private final String accessTokenKey;

    /**
     * Constructor.
     */
    @Autowired
    public JwtTool(@Value("${accessTokenValidTimeInMinutes}") Integer accessTokenValidTimeInMinutes,
        @Value("${refreshTokenValidTimeInMinutes}") Integer refreshTokenValidTimeInMinutes,
        @Value("${tokenKey}") String accessTokenKey) {
        this.accessTokenValidTimeInMinutes = accessTokenValidTimeInMinutes;
        this.refreshTokenValidTimeInMinutes = refreshTokenValidTimeInMinutes;
        this.accessTokenKey = accessTokenKey;
    }

    /**
     * Method for creating access token.
     *
     * @param email this is email of user.
     * @param role  this is role of user.
     */
    public String createAccessToken(String email, Role role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put(AUTHORITIES, Collections.singleton(role.name()));
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, accessTokenValidTimeInMinutes);
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(calendar.getTime())
            .signWith(SignatureAlgorithm.HS256, accessTokenKey)
            .compact();
    }

    /**
     * Method for creating access token.
     *
     * @param user - entity {@link UserVO}
     */
    public String createRefreshToken(UserVO user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put(AUTHORITIES, Collections.singleton(user.getRole().name()));
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, refreshTokenValidTimeInMinutes);
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(calendar.getTime())
            .signWith(SignatureAlgorithm.HS256, user.getRefreshTokenKey())
            .compact();
    }

    /**
     * Gets email from token and throws an error if token is expired. WARNING: The
     * method DOESN'T CHECK whether the token's signature is valid.
     *
     * @param token - access token
     * @return - user's email
     * @throws io.jsonwebtoken.ExpiredJwtException - if token is expired.
     */
    public String getEmailOutOfAccessToken(String token) {
        String[] splitToken = token.split("\\.");
        String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
        DefaultJwtParser parser = new DefaultJwtParser();
        Jwt<?, ?> jwt = parser.parse(unsignedToken);
        return ((Claims) jwt.getBody()).getSubject();
    }

    /**
     * Method that check if token still valid.
     *
     * @param token this is token.
     * @return {@link Boolean}
     */
    public boolean isTokenValid(String token, String tokenKey) {
        boolean isValid = false;
        try {
            Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(token);
            isValid = true;
        } catch (Exception e) {
            log.info("Given token is not valid: " + e.getMessage());
        }
        return isValid;
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
        return Optional
            .ofNullable(servletRequest.getHeader("Authorization"))
            .filter(authHeader -> authHeader.startsWith("Bearer "))
            .map(token -> token.substring(7))
            .orElse(null);
    }

    /**
     * Generates a random string that can be used as refresh token key.
     *
     * @return random generated token key
     */
    public String generateTokenKey() {
        return UUID.randomUUID().toString();
    }
}
