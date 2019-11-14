package greencity.security.jwt;

import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.*;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Value("${accessTokenValidTimeInMinutes}")
    private Integer accessTokenValidTimeInMinutes;

    @Value("${refreshTokenValidTimeInMinutes}")
    private Integer refreshTokenValidTimeInMinutes;
    private final UserService userService;
    private final Function<String, String> tokenToEmailParser;
    private final String tokenKey;

    /**
     * Constructor.
     */
    @Autowired
    public JwtTool(UserService userService, Function<String, String> tokenToEmailParser, String tokenKey) {
        this.userService = userService;
        this.tokenToEmailParser = tokenToEmailParser;
        this.tokenKey = tokenKey;
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
            .signWith(SignatureAlgorithm.HS256, tokenKey)
            .compact();
    }

    /**
     * Method for creating refresh token.
     *
     * @param email this is email of user.
     */
    public String createRefreshToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, refreshTokenValidTimeInMinutes);
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(calendar.getTime())
            .signWith(SignatureAlgorithm.HS256, tokenKey)
            .compact();
    }

    /**
     * Method that check if token still valid.
     *
     * @param token this is token.
     * @return {@link Boolean}
     */
    public boolean isTokenValid(String token) { // TODO - should be factored out of this class
        boolean isValid = false;
        try {
            Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(token);
            isValid = true;
        } catch (Exception e) {
            //if can't parse token
        }
        return isValid;
    }

    /**
     * Method that create authentication.
     *
     * @param token token from request.
     * @return {@link Authentication}
     */
    public Authentication getAuthentication(String token) {
        return userService
                .findByEmail(tokenToEmailParser.apply(token))
                .filter(user -> user.getUserStatus() != UserStatus.DEACTIVATED) // TODO - what if user is BLOCKED ????
                .map(userService::updateLastVisit)
                .map(user -> new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        "",
                        Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
                ))
                .orElse(null);
    }
}
