package greencity.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Class that provide filtering for JWT security
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Slf4j
public class JwtFilter extends GenericFilterBean {

    private JwtTokenTool tool;

    public JwtFilter(JwtTokenTool tool) {
        this.tool = tool;
    }
    /**
     * Method that check if request has token in body, if this token still valid, and set
     * authentication for spring.
     *
     * @param servletRequest this is servlet that take request
     * @param servletResponse this is response servlet
     * @param filterChain this is filter of chain
     */
    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        log.info("begin");
        String token = tool.getTokenByBody((HttpServletRequest) servletRequest);
        if (token != null && tool.isTokenValid(token)) {
            Authentication authentication = tool.getAuthentication(token);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
        log.info("end");
    }
}
