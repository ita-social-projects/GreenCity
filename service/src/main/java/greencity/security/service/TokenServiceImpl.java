package greencity.security.service;

import greencity.exception.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TokenServiceImpl implements TokenService {
    @Value("${use-https}")
    private String security;

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
        Cookie cookie = new Cookie("token", sanitizedToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(security.equals("true"));
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
