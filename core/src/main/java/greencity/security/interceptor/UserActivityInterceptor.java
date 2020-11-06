package greencity.security.interceptor;

import greencity.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * The class provides intercepting and updating last user activity time.
 *
 * @author Yurii Zhurakovskyi
 * @version 1.0
 */
public class UserActivityInterceptor extends HandlerInterceptorAdapter {
    private final UserService userService;

    /**
     * Constructor.
     *
     * @param userService {@link UserService}
     */
    public UserActivityInterceptor(UserService userService) {
        this.userService = userService;
    }

    /**
     * The method finds and updates the last user activity time before handling a
     * request.
     *
     * @param request  provides request information
     * @param response provides functionality in sending a response
     * @param handler  the handler object to handle the request
     * @return a boolean value determines if the request further processed by a
     *         handler or not
     */
    @Override
    public boolean preHandle(@SuppressWarnings("NullableProblems") HttpServletRequest request,
        @SuppressWarnings("NullableProblems") HttpServletResponse response,
        @SuppressWarnings("NullableProblems") Object handler)
        throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String email = authentication.getPrincipal().toString();
            if (!email.equals("anonymousUser")) {
                Long userId = userService.findIdByEmail(email);
                Date userLastActivityTime = new Date();
                userService.updateUserLastActivityTime(userId, userLastActivityTime);
            }
        }
        return super.preHandle(request, response, handler);
    }
}
