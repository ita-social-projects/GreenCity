package greencity.security.providers;

import greencity.security.jwt.JwtTool;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.AUTHORITIES;

/**
 * Class that provides authentication logic.
 *
 * @author Yurii Koval
 * @version 1.1
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtTool jwtTool;

    /**
     * Constructor.
     * 
     * @param jwtTool {@link JwtTool}
     */
    public JwtAuthenticationProvider(JwtTool jwtTool) {
        this.jwtTool = jwtTool;
    }

    /**
     * Method that provide authentication.
     *
     * @param authentication {@link Authentication} - authentication that has jwt
     *                       access token.
     * @return {@link Authentication} if user successfully authenticated.
     * @throws io.jsonwebtoken.ExpiredJwtException   - if the token expired.
     * @throws UnsupportedJwtException               if the argument does not
     *                                               represent an Claims JWS
     * @throws io.jsonwebtoken.MalformedJwtException if the string is not a valid
     *                                               JWS
     * @throws io.jsonwebtoken.SignatureException    if the JWS signature validation
     *                                               fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        String email = Jwts.parser()
            .setSigningKey(jwtTool.getAccessTokenKey())
            .parseClaimsJws(authentication.getName())
            .getBody()
            .getSubject();
        @SuppressWarnings({"unchecked, rawtype"})
        List<String> authorities = (List<String>) Jwts.parser()
            .setSigningKey(jwtTool.getAccessTokenKey())
            .parseClaimsJws(authentication.getName())
            .getBody()
            .get(AUTHORITIES);
        return new UsernamePasswordAuthenticationToken(
            email,
            "",
            authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
