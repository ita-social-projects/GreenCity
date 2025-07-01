package greencity.service;

/**
 * Service interface for handling text translations between languages.
 * <p>
 * Implementations of this interface provide translation functionality from a
 * given source language to a target language.
 * </p>
 *
 * @author Popovych Oleh
 * @version 1.0
 */
public interface TranslationService {
    /**
     * Translates the given text from the source language to the target language.
     *
     * @param text           the text to be translated
     * @param sourceLanguage the language code of the original text (e.g., "en" for
     *                       English)
     * @param targetLanguage the language code of the target translation (e.g., "uk"
     *                       for Ukrainian)
     * @return the translated text
     */
    String translateText(String text, String sourceLanguage, String targetLanguage);
}
