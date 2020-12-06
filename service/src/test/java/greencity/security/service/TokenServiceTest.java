package greencity.security.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl();
    }

    @Test
    void passTokenToCookies() {
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXJ";
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        tokenService.passTokenToCookies(accessToken, mockHttpServletResponse);

        Cookie expectedAccessTokenCookie = Objects.requireNonNull(mockHttpServletResponse.getCookie("accessToken"));

        String expectedAccessTokenValue = expectedAccessTokenCookie.getValue();
        boolean expectedHttpOnlyFlag = expectedAccessTokenCookie.isHttpOnly();
        boolean expectedSecuredFlag = expectedAccessTokenCookie.getSecure();

        assertEquals(expectedAccessTokenValue, accessToken);
        assertTrue(expectedHttpOnlyFlag);
        assertTrue(expectedSecuredFlag);
    }
}