package greencity.service;

import com.deepl.api.DeepLClient;
import com.deepl.api.Formality;
import com.deepl.api.TextResult;
import com.deepl.api.DeepLException;
import com.deepl.api.SentenceSplittingMode;
import com.deepl.api.TextTranslationOptions;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.TranslationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link TranslationService} that uses the DeepL API to
 * perform translations.
 * <p>
 * This service configures translation options such as formality and sentence
 * splitting mode, and handles exceptions that may occur during API calls.
 * </p>
 *
 * <p>
 * Example usage:
 *
 * <pre>
 * translationService.translateText("Hello", "en", "uk");
 * </pre>
 * </p>
 *
 *
 * @author Popovych Oleh
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepLTranslationService implements TranslationService {
    private final DeepLClient deeplClient;

    /**
     * Translates the given text from the specified source language to the target
     * language using DeepL's translation service.
     *
     * @param text           the text to be translated
     * @param sourceLanguage the language code of the input text (e.g., "en")
     * @param targetLanguage the language code of the desired output text (e.g.,
     *                       "uk")
     * @return the translated text
     * @throws TranslationException if the translation fails due to DeepL error or
     *                              interruption
     */
    @Override
    public String translateText(String text, String sourceLanguage, String targetLanguage) {
        try {
            TextTranslationOptions textTranslationOptions = new TextTranslationOptions();
            textTranslationOptions.setFormality(Formality.PreferMore);
            textTranslationOptions.setSentenceSplittingMode(SentenceSplittingMode.All);
            TextResult textResult =
                deeplClient.translateText(text, sourceLanguage, targetLanguage, textTranslationOptions);
            return textResult.getText();
        } catch (DeepLException | InterruptedException exception) {
            log.error("An error occurred during translation, reason: {}", exception.getMessage());
            throw new TranslationException(
                String.format(ErrorMessage.TRANSLATION_PROCESSING_ERROR, sourceLanguage, targetLanguage));
        }
    }
}
