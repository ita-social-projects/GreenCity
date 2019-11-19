package greencity.security.filters;

import greencity.security.jwt.JwtTool;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class AccessTokenAuthenticationFilter extends BasicAuthenticationFilter {
    private final JwtTool jwtTool;

    /**
     * Constructor.
     */
    public AccessTokenAuthenticationFilter(AuthenticationManager authenticationManager, JwtTool jwtTool) {
        super(authenticationManager);
        this.jwtTool = jwtTool;
    }

    /**
     * Method that check if request has token in body, if this token still valid, and set
     * authentication for spring.
     *
     * @param request  this is servlet that take request
     * @param response this is response servlet
     * @param chain     this is filter of chain
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = jwtTool.getTokenFromHttpServletRequest(request);
        if (token != null) {
            if (jwtTool.isTokenValid(token, jwtTool.getAccessTokenKey())) {
                // TODO - move to Provider
                Authentication authentication = jwtTool.getAuthenticationOutOfAccessToken(token);
                log.info("User successfully authenticate - {}", authentication.getPrincipal());
                getAuthenticationManager().authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
