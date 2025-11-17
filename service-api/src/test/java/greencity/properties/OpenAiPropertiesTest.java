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
class OpenAiPropertiesTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private OpenAiProperties openAiProperties;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(OpenAiProperties.class);
        logCaptor.clearLogs();
    }

    @Test
    void getOpenAiKey_shouldReturnValue_whenPropertyExists() {
        when(environment.getProperty("openai.api.key"))
            .thenReturn("openai-secret");

        String result = openAiProperties.getOpenAiKey();

        assertEquals("openai-secret", result);
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getOpenAiKey_shouldThrowException_whenMissing() {
        when(environment.getProperty("openai.api.key"))
            .thenReturn("");

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> openAiProperties.getOpenAiKey());

        assertEquals(ErrorMessage.OPENAI_TOKEN_KEY_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.OPENAI_TOKEN_KEY_NOT_SET));
    }

    @Test
    void getRelevance_shouldReturnValue_whenPropertyExists() {
        when(environment.getProperty("greencity.relevance.enabled"))
            .thenReturn("enabled");

        String result = openAiProperties.getRelevance();

        assertEquals("enabled", result);
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getRelevance_shouldThrowException_whenMissing() {
        when(environment.getProperty("greencity.relevance.enabled"))
            .thenReturn("");

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> openAiProperties.getRelevance());

        assertEquals(ErrorMessage.RELEVANCE_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.RELEVANCE_NOT_SET));
    }

    @Test
    void validateProperties_shouldCallBothMethodsAndLogSuccess() {
        when(environment.getProperty("openai.api.key"))
            .thenReturn("openai-secret");
        when(environment.getProperty("greencity.relevance.enabled"))
            .thenReturn("true");

        openAiProperties.validateProperties();

        assertTrue(logCaptor.getInfoLogs()
            .contains("All OpneAi properties validated successfully."));
    }
}