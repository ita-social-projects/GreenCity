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
class RemoteWebClientPropertiesTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private RemoteWebClientProperties properties;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(RemoteWebClientProperties.class);
        logCaptor.clearLogs();
    }

    @Test
    void getGreencityServerAddress_shouldReturnValue_whenExists() {
        when(environment.getProperty("address"))
            .thenReturn("https://gc.com");

        assertEquals("https://gc.com", properties.getGreencityServerAddress());
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getGreencityServerAddress_shouldThrow_whenMissing() {
        when(environment.getProperty("address")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getGreencityServerAddress());

        assertEquals(ErrorMessage.GREENCITY_SERVER_ADDRESS_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.GREENCITY_SERVER_ADDRESS_NOT_SET));
    }

    @Test
    void getGreencityUserServerAddress_shouldReturnValue_whenExists() {
        when(environment.getProperty("greencityuser.server.address"))
            .thenReturn("https://gcUser.com");

        assertEquals("https://gcUser.com", properties.getGreencityUserServerAddress());
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getGreencityUserServerAddress_shouldThrow_whenMissing() {
        when(environment.getProperty("greencityuser.server.address")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getGreencityUserServerAddress());

        assertEquals(ErrorMessage.GREENCITY_USER_SERVER_ADDRESS_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.GREENCITY_USER_SERVER_ADDRESS_NOT_SET));
    }

    @Test
    void getGreencityUbsServerAddress_shouldReturnValue_whenExists() {
        when(environment.getProperty("greencityubs.server.address"))
            .thenReturn("https://ubs.com");

        assertEquals("https://ubs.com", properties.getGreencityUbsServerAddress());
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getGreencityUbsServerAddress_shouldThrow_whenMissing() {
        when(environment.getProperty("greencityubs.server.address")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getGreencityUbsServerAddress());

        assertEquals(ErrorMessage.GREENCITY_UBS_SERVER_ADDRESS_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.GREENCITY_UBS_SERVER_ADDRESS_NOT_SET));
    }

    @Test
    void getClientAddress_shouldReturnValue_whenExists() {
        when(environment.getProperty("client.address"))
            .thenReturn("https://client.com");

        assertEquals("https://client.com", properties.getClientAddress());
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getClientAddress_shouldThrow_whenMissing() {
        when(environment.getProperty("client.address")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getClientAddress());

        assertEquals(ErrorMessage.CLIENT_ADDRESS_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.CLIENT_ADDRESS_NOT_SET));
    }

    @Test
    void getConnectionTimeout_shouldReturnValue_whenExists() {
        when(environment.getProperty("webclient.connection-timeout-millis", Integer.class))
            .thenReturn(5000);

        assertEquals(5000, properties.getConnectionTimeout());
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getConnectionTimeout_shouldThrow_whenMissing() {
        when(environment.getProperty("webclient.connection-timeout-millis", Integer.class))
            .thenReturn(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getConnectionTimeout());

        assertEquals(ErrorMessage.WEBCLIENT_CONNECTION_TIMEOUT_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.WEBCLIENT_CONNECTION_TIMEOUT_NOT_SET));
    }

    @Test
    void getResponseTimeout_shouldReturnValue_whenExists() {
        when(environment.getProperty("webclient.response-timeout-millis", Integer.class))
            .thenReturn(7000);

        assertEquals(7000, properties.getResponseTimeout());
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getResponseTimeout_shouldThrow_whenMissing() {
        when(environment.getProperty("webclient.response-timeout-millis", Integer.class))
            .thenReturn(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getResponseTimeout());

        assertEquals(ErrorMessage.WEBCLIENT_RESPONSE_TIMEOUT_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.WEBCLIENT_RESPONSE_TIMEOUT_NOT_SET));
    }

    @Test
    void getSystemEmailAddress_shouldReturnValue_whenExists() {
        when(environment.getProperty("spring.liquibase.parameters.service-email"))
            .thenReturn("system@gmail.com");

        assertEquals("system@gmail.com", properties.getSystemEmailAddress());
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getSystemEmailAddress_shouldThrow_whenMissing() {
        when(environment.getProperty("spring.liquibase.parameters.service-email")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getSystemEmailAddress());

        assertEquals(ErrorMessage.SYSTEM_EMAIL_ADDRESS_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.SYSTEM_EMAIL_ADDRESS_NOT_SET));
    }

    @Test
    void validateProperties_shouldLogSuccess_whenAllPropertiesValid() {
        when(environment.getProperty("address")).thenReturn("a");
        when(environment.getProperty("greencityuser.server.address")).thenReturn("https://gcUser.com");
        when(environment.getProperty("greencityubs.server.address")).thenReturn("b");
        when(environment.getProperty("client.address")).thenReturn("c");
        when(environment.getProperty("webclient.connection-timeout-millis", Integer.class)).thenReturn(1000);
        when(environment.getProperty("webclient.response-timeout-millis", Integer.class)).thenReturn(2000);
        when(environment.getProperty("spring.liquibase.parameters.service-email")).thenReturn("system@gmail.com");

        properties.validateProperties();

        assertTrue(logCaptor.getInfoLogs()
            .contains("All remoteWebClient properties validated successfully."));
    }

    @Test
    void validateProperties_shouldThrow_whenAnyPropertyMissing() {
        when(environment.getProperty("address")).thenReturn("");
        when(environment.getProperty("greencityuser.server.address")).thenReturn("https://gcUser.com");
        when(environment.getProperty("greencityubs.server.address")).thenReturn("ok");
        when(environment.getProperty("client.address")).thenReturn("ok");
        when(environment.getProperty("webclient.connection-timeout-millis", Integer.class)).thenReturn(1000);
        when(environment.getProperty("webclient.response-timeout-millis", Integer.class)).thenReturn(2000);
        when(environment.getProperty("spring.liquibase.parameters.service-email")).thenReturn("system@gmail.com");

        assertThrows(IllegalStateException.class,
            () -> properties.validateProperties());

        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.GREENCITY_SERVER_ADDRESS_NOT_SET));
    }
}