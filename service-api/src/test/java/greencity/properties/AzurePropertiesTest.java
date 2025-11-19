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
class AzurePropertiesTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private AzureProperties azureProperties;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(AzureProperties.class);
        logCaptor.clearLogs();
    }

    @Test
    void getAzureConnectionString_shouldReturnValue_whenPropertyExists() {
        when(environment.getProperty("azure.connection.string"))
            .thenReturn("DefaultEndpointsProtocol=https;AccountName=test;");

        String result = azureProperties.getAzureConnectionString();

        assertEquals("DefaultEndpointsProtocol=https;AccountName=test;", result);
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getAzureConnectionString_shouldThrowException_whenPropertyMissing() {
        when(environment.getProperty("azure.connection.string")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> azureProperties.getAzureConnectionString());

        assertEquals(ErrorMessage.AZURE_CONNECTION_STRING_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.AZURE_CONNECTION_STRING_NOT_SET));
    }

    @Test
    void getAzureContainerName_shouldReturnValue_whenPropertyExists() {
        when(environment.getProperty("azure.container.name"))
            .thenReturn("images");

        String result = azureProperties.getAzureContainerName();

        assertEquals("images", result);
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getAzureContainerName_shouldThrowException_whenPropertyMissing() {
        when(environment.getProperty("azure.container.name")).thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> azureProperties.getAzureContainerName());

        assertEquals(ErrorMessage.AZURE_CONTAINER_NAME_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.AZURE_CONTAINER_NAME_NOT_SET));
    }

    @Test
    void validateProperties_shouldLogSuccess_whenAllPropertiesExist() {
        when(environment.getProperty("azure.connection.string"))
            .thenReturn("connection123");
        when(environment.getProperty("azure.container.name"))
            .thenReturn("container123");

        azureProperties.validateProperties();

        assertTrue(logCaptor.getInfoLogs()
            .contains("All azure properties validated successfully."));
    }

    @Test
    void validateProperties_shouldThrowException_whenAnyPropertyMissing() {
        when(environment.getProperty("azure.connection.string")).thenReturn("");
        when(environment.getProperty("azure.container.name")).thenReturn("valid");

        assertThrows(IllegalStateException.class,
            () -> azureProperties.validateProperties());

        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.AZURE_CONNECTION_STRING_NOT_SET));
    }
}