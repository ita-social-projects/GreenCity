package greencity.service;

import com.deepl.api.DeepLClient;
import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.TranslationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeepLTranslationServiceTest {
    @Mock
    private DeepLClient deeplClient;

    @InjectMocks
    private DeepLTranslationService translationService;

    @Test
    void translateText_success_returnsTranslatedText() throws Exception {
        String input = "Hello";
        String expected = "Привіт";
        TextResult result = mock(TextResult.class);

        when(result.getText()).thenReturn(expected);
        when(deeplClient.translateText(eq(input), eq("en"), eq("uk"), any()))
            .thenReturn(result);

        String actual = translationService.translateText(input, "en", "uk");

        assertEquals(expected, actual);
        verify(deeplClient).translateText(eq(input), eq("en"), eq("uk"), any());
    }

    @Test
    void translateText_whenDeepLExceptionThrown_throwsTranslationException() throws Exception {
        when(deeplClient.translateText(anyString(), anyString(), anyString(), any()))
                .thenThrow(new DeepLException("DeepL API error"));

        TranslationException exception = assertThrows(
                TranslationException.class,
                () -> translationService.translateText("Hello", "en", "uk")
        );

        assertEquals(String.format(ErrorMessage.TRANSLATION_PROCESSING_ERROR, "en", "uk"), exception.getMessage());
    }

    @Test
    void translateText_whenInterruptedExceptionThrown_throwsTranslationException() throws Exception {
        when(deeplClient.translateText(anyString(), anyString(), anyString(), any()))
                .thenThrow(new InterruptedException("Thread interrupted"));

        TranslationException exception = assertThrows(
                TranslationException.class,
                () -> translationService.translateText("Hello", "en", "uk")
        );

        assertEquals(String.format(ErrorMessage.TRANSLATION_PROCESSING_ERROR, "en", "uk"), exception.getMessage());
    }
}
