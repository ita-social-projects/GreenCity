package greencity.security.jwt;

import greencity.entity.User;
import greencity.entity.enums.ROLE;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultJwtParser;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Class that provide Jwt token logic.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Component
@Slf4j
public class JwtTool {
    private final Integer accessTokenValidTimeInMinutes;
    private final Integer refreshTokenValidTimeInMinutes;
    private final String accessTokenKey;

    /**
     * Constructor.
     */
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
    public String createAccessToken(String email, ROLE role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", Collections.singleton(role.name()));
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
     * @param user - entity {@link User}
     */
    public String createRefreshToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("roles", Collections.singleton(user.getRole().name()));
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
     * Gets email from token.
     *
     * @param token - access token
     * @return - user's email
     */
    public String getEmailOutOfAccessToken(String token) {
        String[] splitToken = token.split("\\.");
        String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
        DefaultJwtParser parser = new DefaultJwtParser();
        Jwt<?, ?> jwt = parser.parse(unsignedToken);
        return ((Claims) jwt.getBody()).getSubject();
    }

    /**
     * Gets Authentication from token.
     *
     * @param token - access token.
     * @return - Authentication
     */
    public Authentication getAuthenticationOutOfAccessToken(String token) {
        String email = Jwts.parser()
            .setSigningKey(accessTokenKey)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
        List authorities = Jwts.parser()
            .setSigningKey(accessTokenKey)
            .parseClaimsJws(token)
            .getBody()
            .get("roles", List.class);
        return new UsernamePasswordAuthenticationToken(
            email,
            "",
            Collections.singleton(new SimpleGrantedAuthority((String) authorities.get(0)))
        );
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
     * Method that get token by body request.
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
     * Generates random string.
     *
     * @return random generated token key
     */
    public String generateRefreshTokenKey() {
        return UUID.randomUUID().toString();
    }
}
