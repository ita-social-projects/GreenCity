package greencity.security.service;

import greencity.exception.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {
    private Environment environment;

    /**
     * {@inheritDoc}
     */
    @Override
    public void passTokenToCookies(String accessToken, HttpServletResponse response) {
        String checkToken = "eyJhbGciOiJIUzI1NiJ9";
        if (!accessToken.contains(checkToken)) {
            throw new BadRequestException("bad access token");
        }

        String sanitizedToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        Cookie cookie = new Cookie("accessToken", sanitizedToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(isProdProfile());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private boolean isProdProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProd = false;
        for (String profile : activeProfiles) {
            if ("prod".equalsIgnoreCase(profile)) {
                isProd = true;
                break;
            }
        }
        return isProd;
    }
}
