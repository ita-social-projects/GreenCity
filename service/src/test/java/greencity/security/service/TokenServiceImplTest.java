package greencity.security.service;

import greencity.exception.exceptions.BadRequestException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @Mock
    private Environment environment;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Test
    void testPassTokenToCookies_ProdEnvironment_SecureTrue() {
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.validToken";
        String[] activeProfiles = {"prod"};
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);

        tokenService.passTokenToCookies(accessToken, response);

        verify(response).addCookie(argThat(cookie -> {
            assertEquals("token", cookie.getName());
            assertEquals(URLEncoder.encode(accessToken, StandardCharsets.UTF_8), cookie.getValue());
            assertTrue(cookie.isHttpOnly());
            assertTrue(cookie.getSecure());
            assertEquals("/", cookie.getPath());
            return true;
        }));
    }

    @Test
    void testPassTokenToCookies_NonProdEnvironment_SecureFalse() {
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.validToken";
        String[] activeProfiles = {"dev"};
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);

        tokenService.passTokenToCookies(accessToken, response);

        verify(response).addCookie(argThat(cookie -> {
            assertEquals("token", cookie.getName());
            assertEquals(URLEncoder.encode(accessToken, StandardCharsets.UTF_8), cookie.getValue());
            assertTrue(cookie.isHttpOnly());
            assertFalse(cookie.getSecure());
            assertEquals("/", cookie.getPath());
            return true;
        }));
    }

    @Test
    void testPassTokenToCookies_InvalidToken_ThrowsBadRequestException() {
        String accessToken = "invalidToken";

        BadRequestException thrown = assertThrows(
            BadRequestException.class,
            () -> tokenService.passTokenToCookies(accessToken, response),
            "Expected passTokenToCookies to throw, but it didn't");

        assertEquals("bad access token", thrown.getMessage());
        verify(response, never()).addCookie(any(Cookie.class));
    }
}
