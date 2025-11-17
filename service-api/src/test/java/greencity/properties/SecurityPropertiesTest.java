package greencity.properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import greencity.constant.ErrorMessage;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityPropertiesTest {
    @Mock
    private Environment environment;

    @InjectMocks
    private SecurityProperties properties;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(SecurityProperties.class);
        logCaptor.clearLogs();
    }

    @Test
    void getAccessTokenKey_shouldReturnValue_whenExists() {
        when(environment.getProperty("security.jwt.secret-key"))
            .thenReturn("super-secret");

        assertEquals("super-secret", properties.getAccessTokenKey());
    }

    @Test
    void getAccessTokenKey_shouldThrow_whenMissing() {
        when(environment.getProperty("security.jwt.secret-key"))
            .thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getAccessTokenKey());

        assertEquals(ErrorMessage.ACCESS_TOKEN_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.ACCESS_TOKEN_NOT_SET));
    }

    @Test
    void getJwtAccessTokenExpiration_shouldReturnValue_whenExists() {
        when(environment.getProperty("security.jwt.access-token.expiration-minutes", Integer.class))
            .thenReturn(5);

        assertEquals(5, properties.getJwtAccessTokenExpiration());
    }

    @Test
    void getJwtAccessTokenExpiration_shouldThrow_whenMissing() {
        when(environment.getProperty("security.jwt.access-token.expiration-minutes", Integer.class))
            .thenReturn(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getJwtAccessTokenExpiration());

        assertEquals(ErrorMessage.JWT_ACCESS_TOKEN_EXPIRATION_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.JWT_ACCESS_TOKEN_EXPIRATION_NOT_SET));
    }

    @Test
    void validateProperties_shouldLogSuccess_whenAllPropertiesValid() {
        when(environment.getProperty("security.jwt.secret-key"))
            .thenReturn("key");
        when(environment.getProperty("security.jwt.access-token.expiration-minutes", Integer.class))
            .thenReturn(5);

        properties.validateProperties();

        assertTrue(logCaptor.getInfoLogs()
            .contains("All security properties validated successfully."));
    }

    @Test
    void validateProperties_shouldThrow_whenAnyPropertyMissing() {
        when(environment.getProperty("security.jwt.secret-key"))
            .thenReturn("");
        when(environment.getProperty("security.jwt.access-token.expiration-minutes", Integer.class))
            .thenReturn(5);

        assertThrows(IllegalStateException.class,
            () -> properties.validateProperties());

        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.ACCESS_TOKEN_NOT_SET));
    }
}