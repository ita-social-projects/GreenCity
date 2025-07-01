package greencity.service;

/**
 * Provides the interface to handle translations.
 *
 * @author Popovych Oleh
 * @version 1.0
 */
public interface TranslationService {
    String translateText(String text, String sourceLanguage, String targetLanguage);
}
