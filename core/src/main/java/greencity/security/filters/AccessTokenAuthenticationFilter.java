package greencity.security.filters;

import greencity.entity.User;
import greencity.entity.enums.UserStatus;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class that provide Authentication object based on JWT.
 *
 * @author Yurii Koval.
 * @version 1.0
 */
@Slf4j
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTool jwtTool;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    /**
     * Constructor.
     */
    public AccessTokenAuthenticationFilter(JwtTool jwtTool, AuthenticationManager authenticationManager,
                                           UserService userService) {
        this.jwtTool = jwtTool;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    /**
     * Checks if request has token in header, if this token still valid, and set
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
        String token = jwtTool.getTokenFromHttpServletRequest(request);

        if (token != null) {
            try {
                Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(token, null));
                User user = userService.findByEmail((String) authentication.getPrincipal());
                if (user.getUserStatus() != UserStatus.DEACTIVATED) {
                    log.info("User successfully authenticate - {}", authentication.getPrincipal());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    authenticationManager.authenticate(null);
                    return;
                }
            } catch (ExpiredJwtException e) {
                log.info("Token has expired: " + token);
            } catch (Exception e) {
                log.info("Access denied with token: " + e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}
