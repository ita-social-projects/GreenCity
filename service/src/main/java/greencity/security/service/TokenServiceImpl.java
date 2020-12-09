package greencity.security.service;

import greencity.exception.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class TokenServiceImpl implements TokenService {
    /**
     * {@inheritDoc}
     */
    @Override
    public void passTokenToCookies(String accessToken, HttpServletResponse response) {
        String whitelist = "ey";
        if (!accessToken.contains(whitelist)) {
            throw new BadRequestException("bad access token");
        }
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
