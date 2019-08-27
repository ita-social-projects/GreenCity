package greencity.security;

import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import greencity.entity.enums.ROLE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenTool {

    @Value("${tokenValidTimeInMinutes}")
    private Integer tokenValidTimeInMinutes;

    @Value("${tokenKey}")
    private String tokenKey;

    private JwtUserDetailService userDetailsService;

    public JwtTokenTool(JwtUserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    void init() {
        tokenKey = Base64.getEncoder().encodeToString(tokenKey.getBytes());
    }

    public String createAccessToken(String email, ROLE role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", Collections.singleton(role));
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, tokenValidTimeInMinutes);

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(calendar.getTime())
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, tokenKey)
                .compact();
    }

    public String createRefreshToken() {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, tokenValidTimeInMinutes);

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, tokenKey)
                .setIssuedAt(new Date())
                .setExpiration(calendar.getTime())
                .compact();
    }

    public boolean isAccessTokenValid(String token) {
        boolean isValid = false;
        try {
            Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(token);
            isValid = true;
        } catch (Exception e) {

        }
        return isValid;
    }

    public boolean isRefreshTokenValid(String token) {
        boolean isValid = false;
        try {
            Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(token);
            isValid = true;
        } catch (Exception e) {

        }
        return isValid;
    }

    public String getTokenByBody(HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7, token.length());
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getEmailByToken(token));
        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
    }

    public String getEmailByToken(String token) {
        return Jwts.parser().setSigningKey(tokenKey).parseClaimsJwt(token).getBody().getSubject();
    }
}
