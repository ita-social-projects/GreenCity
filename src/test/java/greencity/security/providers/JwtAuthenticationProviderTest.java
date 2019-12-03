package greencity.security.providers;

import greencity.security.jwt.JwtTool;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationProvider;

public class JwtAuthenticationProviderTest {

    @Mock
    JwtTool jwtTool;
    private AuthenticationProvider jwtAuthenticationProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtTool);
    }

    @Test
    public void authenticateValidToken() {
        when(jwtTool.getAccessTokenKey()).thenReturn("123");
    }
}