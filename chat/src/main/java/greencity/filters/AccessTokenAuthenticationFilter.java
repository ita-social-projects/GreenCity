package greencity.filters;

import greencity.jwt.JwtTool;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Class that provide Authentication object based on JWT.
 */
@Slf4j
@AllArgsConstructor
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTool jwtTool;
    private final AuthenticationManager authenticationManager;

    private String getTokenFromCookies(Cookie[] cookies) {
        return Arrays.stream(cookies)
            .filter(c -> c.getName().equals("accessToken"))
            .findFirst()
            .map(Cookie::getValue).orElse(null);
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String uri = request.getRequestURI();
        if (cookies != null && uri.startsWith("/management")) {
            return getTokenFromCookies(cookies);
        }

        return jwtTool.getTokenFromHttpServletRequest(request);
    }

    /**
     * Checks if request has token in header (if this token still valid) and sets
     * authentication for spring.
     *
     * @param request  this is servlet that take request
     * @param response this is response servlet
     * @param chain    this is filter of chain
     */
    @Override
    public void doFilterInternal(@SuppressWarnings("NullableProblems") HttpServletRequest request,
        @SuppressWarnings("NullableProblems") HttpServletResponse response,
        @SuppressWarnings("NullableProblems") FilterChain chain)
        throws IOException, ServletException {
        String token = extractToken(request);

        if (token != null) {
            try {
                Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(token, null));
                log.debug("User successfully authenticate - {}", authentication.getPrincipal());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                log.info("Token has expired: " + token);
            } catch (Exception e) {
                log.info("Access denied with token: " + e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}
