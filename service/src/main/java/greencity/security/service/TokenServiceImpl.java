package greencity.security.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class TokenServiceImpl implements TokenService {

    /**
     * {@inheritDoc}
     * */
    @Override
    public void passTokenToCookies(String accessToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
