package greencity.service;

import static greencity.constant.GrammarCheckConstants.ENGLISH_PREFIX;
import static greencity.constant.GrammarCheckConstants.ERROR_CHECKING_GRAMMAR;
import greencity.exception.exceptions.GrammarCheckException;
import java.io.IOException;
import java.util.List;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service class for checking and correcting grammar in text.
 * This class uses JLanguageTool for grammar checking and Apache Tika for language detection.
 */
@Service
public class GrammarChecker implements GrammarCheckerService {
    private final JLanguageTool enLangTool;
    private final JLanguageTool uaLangTool;
    private final LanguageDetector languageDetector;

    /**
     * Constructs a GrammarChecker with the specified language tools and language detector.
     *
     * @param enLangTool the JLanguageTool instance for English language
     * @param uaLangTool the JLanguageTool instance for Ukrainian language
     * @param languageDetector the LanguageDetector instance for detecting the language of the text
     */
    public GrammarChecker(@Qualifier("englishTool") JLanguageTool enLangTool,
                          @Qualifier("ukrainianTool") JLanguageTool uaLangTool,
                          LanguageDetector languageDetector) {
        this.enLangTool = enLangTool;
        this.uaLangTool = uaLangTool;
        this.languageDetector = languageDetector;
    }

    /**
     * Checks and corrects the grammar of the given text.
     *
     * @param text the text to be checked and corrected
     * @return the corrected text
     * @throws IOException if an I/O error occurs during grammar checking
     * @throws GrammarCheckException if an error occurs during grammar checking
     */
    @Override
    public String checkGrammar(String text) throws IOException {
        try {
            String detectedLanguage = detectLanguage(text);
            JLanguageTool languageTool = getLanguageTool(detectedLanguage);

            List<RuleMatch> matches = languageTool.check(text);
            if (matches.isEmpty()) {
                return text;
            }
            return applyConnections(text, matches);
        } catch (Exception e) {
            throw new GrammarCheckException(ERROR_CHECKING_GRAMMAR, e);
        }
    }

    /**
     * Detects the language of the given text.
     *
     * @param text the text whose language is to be detected
     * @return the detected language code (e.g., "en" for English, "uk" for Ukrainian)
     */
    private String detectLanguage(String text) {
        LanguageResult result = languageDetector.detect(text);
        return result.getLanguage();
    }

    /**
     * Returns the appropriate JLanguageTool instance based on the detected language.
     * Defaults to Ukrainian if the language is not English.
     *
     * @param language the detected language code
     * @return the JLanguageTool instance for the detected language
     */
    private JLanguageTool getLanguageTool(String language) {
        return ENGLISH_PREFIX.equals(language) ? enLangTool : uaLangTool;
    }

    /**
     * Applies the suggested corrections to the text based on the list of RuleMatch objects.
     *
     * @param text the original text
     * @param matches the list of RuleMatch objects containing the grammar errors and suggested corrections
     * @return the corrected text
     */
    private String applyConnections(String text, List<RuleMatch> matches) {
        StringBuilder correctedText = new StringBuilder(text);
        int offset = 0;

        for (RuleMatch match : matches) {
            List<String> replacements = match.getSuggestedReplacements();

            if (!replacements.isEmpty()) {
                String replacement = replacements.getFirst();
                int start = match.getFromPos() + offset;
                int end = match.getToPos() + offset;

                correctedText.replace(start, end, replacement);
                offset += replacement.length() - (end - start);
            }
        }
        return correctedText.toString();
    }
}
