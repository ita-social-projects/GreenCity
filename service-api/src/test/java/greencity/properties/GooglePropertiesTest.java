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
class GooglePropertiesTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private GoogleProperties googleProperties;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(GoogleProperties.class);
        logCaptor.clearLogs();
    }

    @Test
    void getGoogleApiKey_shouldReturnValue_whenPropertyExists() {
        when(environment.getProperty("greencity.authorization.googleApiKey"))
            .thenReturn("google-secret-key");

        String result = googleProperties.getGoogleApiKey();

        assertEquals("google-secret-key", result);
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getGoogleApiKey_shouldThrowException_whenMissing() {
        when(environment.getProperty("greencity.authorization.googleApiKey")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> googleProperties.getGoogleApiKey());

        assertEquals(ErrorMessage.GOOGLE_API_KEY_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.GOOGLE_API_KEY_NOT_SET));
    }

    @Test
    void getGoogleMapApiKey_shouldReturnValue_whenPropertyExists() {
        when(environment.getProperty("google.maps.api.key"))
            .thenReturn("google-map-api-secret-key");

        String result = googleProperties.getGoogleMapApiKey();

        assertEquals("google-map-api-secret-key", result);
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getGoogleMapApiKey_shouldThrowException_whenMissing() {
        when(environment.getProperty("google.maps.api.key")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> googleProperties.getGoogleMapApiKey());

        assertEquals(ErrorMessage.GOOGLE_MAP_API_KEY_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.GOOGLE_MAP_API_KEY_NOT_SET));
    }

    @Test
    void validateProperties_shouldLogSuccess_whenPropertyValid() {
        when(environment.getProperty("greencity.authorization.googleApiKey"))
            .thenReturn("valid-key");
        when(environment.getProperty("google.maps.api.key"))
            .thenReturn("google-map-api-secret-key");

        googleProperties.validateProperties();

        assertTrue(logCaptor.getInfoLogs()
            .contains("All google properties validated successfully."));
    }

    @Test
    void validateProperties_shouldThrowException_whenPropertyMissing() {
        when(environment.getProperty("greencity.authorization.googleApiKey")).thenReturn("");
        when(environment.getProperty("google.maps.api.key"))
            .thenReturn("google-map-api-secret-key");

        assertThrows(IllegalStateException.class,
            () -> googleProperties.validateProperties());

        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.GOOGLE_API_KEY_NOT_SET));
    }
}