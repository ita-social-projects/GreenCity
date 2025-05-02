package greencity.scheduler;

import greencity.exception.exceptions.EcoNewsGenerationLimitException;
import greencity.exception.exceptions.GrammarCheckException;
import greencity.service.AIService;
import greencity.service.AcceptLanguageDisplayService;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

@ExtendWith(MockitoExtension.class)
class EcoNewsGenerationJobTest {
    @Mock
    private AIService aiService;
    @Mock
    private AcceptLanguageDisplayService acceptLanguageDisplayService;
    @InjectMocks
    private EcoNewsGenerationJob ecoNewsGenerationJob;
    @Mock
    private JobExecutionContext jobExecutionContext;

    @Test
    void testExecute_ShouldCallGenerateEcoNewsBasedOnHabits() {
        String expectedLanguage = "en";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(expectedLanguage);
        ecoNewsGenerationJob.execute(jobExecutionContext);
        verify(aiService).generateEcoNewsBasedOnHabits(expectedLanguage);
    }

    @Test
    void testExecute_ShouldThrowException_WhenServiceFails() {
        String language = "en";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(language);
        doThrow(new EcoNewsGenerationLimitException("Too soon"))
            .when(aiService).generateEcoNewsBasedOnHabits(language);

        assertThrows(EcoNewsGenerationLimitException.class, () ->
            ecoNewsGenerationJob.execute(jobExecutionContext));
    }

    @Test
    void testExecute_ShouldThrowException_WhenLanguageResolutionFails() {
        when(acceptLanguageDisplayService.resolveLanguage())
            .thenThrow(new RuntimeException("Unexpected"));

        assertThrows(RuntimeException.class, () ->
            ecoNewsGenerationJob.execute(jobExecutionContext));
    }

    @Test
    void testExecute_ShouldCallGenerateEcoNewsBasedOnHabits_WithDifferentLanguages() {
        String language = "ua";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(language);
        ecoNewsGenerationJob.execute(jobExecutionContext);
        verify(aiService).generateEcoNewsBasedOnHabits(language);
    }

    @Test
    void testExecute_ShouldNotThrowException_WhenServiceSucceeds() {
        String language = "en";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(language);
        assertDoesNotThrow(() -> ecoNewsGenerationJob.execute(jobExecutionContext));
        verify(aiService).generateEcoNewsBasedOnHabits(language);
        verifyNoMoreInteractions(aiService);
    }

    @Test
    void testExecute_ShouldHandleNullJobExecutionContext() {
        String language = "en";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(language);
        assertDoesNotThrow(() -> ecoNewsGenerationJob.execute(null));
        verify(aiService).generateEcoNewsBasedOnHabits(language);
    }

    @Test
    void testExecute_ShouldThrowGrammarCheckException_WhenGrammarCheckFails() {
        String language = "en";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(language);
        GrammarCheckException exception =
            new GrammarCheckException("Grammar check failed", new Throwable("Some cause"));
        doThrow(exception).when(aiService).generateEcoNewsBasedOnHabits(language);
        assertThrows(GrammarCheckException.class, () -> ecoNewsGenerationJob.execute(jobExecutionContext));
    }

    @Test
    void testExecute_ShouldThrowException_WhenSavingEcoNewsFails() {
        String language = "en";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(language);
        doThrow(new RuntimeException("Database error")).when(aiService).generateEcoNewsBasedOnHabits(language);
        assertThrows(RuntimeException.class, () -> ecoNewsGenerationJob.execute(jobExecutionContext));
    }

    @Test
    void testExecute_ShouldHandleInvalidLanguage() {
        String invalidLanguage = "zz";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(invalidLanguage);
        assertDoesNotThrow(() -> ecoNewsGenerationJob.execute(jobExecutionContext));
        verify(aiService).generateEcoNewsBasedOnHabits(invalidLanguage);
    }

    @Test
    void testExecute_ShouldHandleLanguageWithSpecialCharacters() {
        String languageWithSpecialChars = "en-US";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(languageWithSpecialChars);
        assertDoesNotThrow(() -> ecoNewsGenerationJob.execute(jobExecutionContext));
        verify(aiService).generateEcoNewsBasedOnHabits(languageWithSpecialChars);
    }

    @Test
    void testExecute_ShouldCallGenerateEcoNewsWithDifferentLanguage() {
        String language = "de";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(language);
        ecoNewsGenerationJob.execute(jobExecutionContext);
        verify(aiService).generateEcoNewsBasedOnHabits(language);
    }

    @Test
    void testExecute_ShouldNotCallGenerateEcoNewsBasedOnHabits_WhenLanguageIsEmpty() {
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn("");
        assertDoesNotThrow(() -> ecoNewsGenerationJob.execute(jobExecutionContext));
        verify(aiService, never()).generateEcoNewsBasedOnHabits(anyString());
    }

    @Test
    void testExecute_ShouldCallGenerateEcoNewsBasedOnHabits_WithLanguageWithDialect() {
        String languageWithDialect = "en-GB";
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(languageWithDialect);
        ecoNewsGenerationJob.execute(jobExecutionContext);
        verify(aiService).generateEcoNewsBasedOnHabits(languageWithDialect);
    }

    @Test
    void testExecute_ShouldCallGenerateEcoNewsBasedOnHabitsOnlyOnce_WhenCalledMultipleTimes() {
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn("en");
        ecoNewsGenerationJob.execute(jobExecutionContext);
        verify(aiService, times(1))
            .generateEcoNewsBasedOnHabits("en");
        ecoNewsGenerationJob.execute(jobExecutionContext);
        verify(aiService, times(1))
            .generateEcoNewsBasedOnHabits("en");
    }

    @Test
    void testExecute_ShouldHandleNullLanguageGracefully() {
        when(acceptLanguageDisplayService.resolveLanguage()).thenReturn(null);

        assertDoesNotThrow(() -> ecoNewsGenerationJob.execute(jobExecutionContext));
        verify(aiService, never()).generateEcoNewsBasedOnHabits(any());
    }

    @Test
    void testExecute_ShouldCallOnlyOnce_WhenAlreadyExecuted() {
        String language = "en";
        when(acceptLanguageDisplayService.resolveLanguage())
            .thenReturn(language);
        ecoNewsGenerationJob.execute(jobExecutionContext);
        ecoNewsGenerationJob.execute(jobExecutionContext);
        verify(aiService, times(1)).generateEcoNewsBasedOnHabits(language);
    }
}
