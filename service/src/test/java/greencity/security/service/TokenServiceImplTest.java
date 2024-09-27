package greencity.security.service;

import greencity.exception.exceptions.BadRequestException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TokenServiceImplTest {

    @InjectMocks
    TokenServiceImpl tokenService;

    @Test
    void passTokenToCookies_validToken() {
        String validToken = "eyJhbGciOiJIUzI1NiJ9.someValidTokenData";
        MockHttpServletResponse response = new MockHttpServletResponse();

        tokenService.passTokenToCookies(validToken, response);

        Cookie[] cookies = response.getCookies();
        assertEquals(1, cookies.length, "There should be one cookie.");
        Cookie tokenCookie = cookies[0];
        assertEquals("token", tokenCookie.getName());
        assertEquals(URLEncoder.encode(validToken, StandardCharsets.UTF_8), tokenCookie.getValue());
        assertTrue(tokenCookie.isHttpOnly());
        assertTrue(tokenCookie.getSecure());
        assertEquals("/", tokenCookie.getPath());
    }

    @Test
    void passTokenToCookies_invalidToken() {
        String invalidToken = "invalidTokenWithoutCheckToken";
        HttpServletResponse response = mock(HttpServletResponse.class);

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            tokenService.passTokenToCookies(invalidToken, response);
        });

        assertEquals("bad access token", thrown.getMessage());
        verify(response, never()).addCookie(any(Cookie.class));
    }
}
