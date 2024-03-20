package greencity.security.filters;

import greencity.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UserOnlineStatusUpdateFilter extends OncePerRequestFilter {
    private final UserService userService;

    private final ThreadPoolTaskExecutor customThreadPool;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        customThreadPool.execute(() -> {
            if (authentication != null) {
                userService.updateUserLastActivityTimeByEmail(authentication.getName(), LocalDateTime.now());
            }
        });
        filterChain.doFilter(request, response);
    }
}
