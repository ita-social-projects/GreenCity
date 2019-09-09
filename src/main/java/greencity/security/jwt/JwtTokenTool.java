package greencity.security.jwt;

import greencity.entity.enums.ROLE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Class that provide Jwt token logic.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Component
@Slf4j
public class JwtTokenTool {
    @Value("${accessTokenValidTimeInMinutes}")
    private Integer accessTokenValidTimeInMinutes;

    @Value("${refreshTokenValidTimeInMinutes}")
    private Integer refreshTokenValidTimeInMinutes;

    @Value("${tokenKey}")
    private String tokenKey;

    private JwtUserDetailService userDetailsService;

    /**
     * Generated javadoc, must be replaced with real one.
     */
    public JwtTokenTool(JwtUserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    void init() {
        tokenKey = Base64.getEncoder().encodeToString(tokenKey.getBytes());
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
    public boolean isTokenValid(String token) {
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
     * Method that get token by body request.
     *
     * @param servletRequest this is your request.
     * @return {@link String} of token or null.
     */
    public String getTokenByBody(HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    /**
     * Method that create authentication.
     *
     * @param token token from request
     * @return {@link Authentication}
     */
    public Authentication getAuthentication(String token) {
        log.info("begin");
        UserDetails userDetails = userDetailsService.loadUserByUsername(getEmailByToken(token));
        log.info("end");
        return new UsernamePasswordAuthenticationToken(
            userDetails, "", userDetails.getAuthorities());
    }

    /**
     * Method that get email from token.
     *
     * @param token token from request.
     * @return {@link String} of email.
     */
    public String getEmailByToken(String token) {
        return Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(token).getBody().getSubject();
    }
}
