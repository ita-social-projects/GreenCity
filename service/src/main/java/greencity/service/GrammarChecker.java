package greencity.service;

import java.util.Objects;
import org.springframework.cache.Cache;
import static greencity.constant.GrammarCheckConstants.*;
import greencity.exception.exceptions.GrammarCheckException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service class for checking and correcting grammar in text. This class
 * integrates with JLanguageTool for grammar checking and OpenNLP for language
 * detection. It also supports caching of results for efficiency. The main flow
 * is: 1. Check grammar using the `checkGrammar` method. 2. Detect the language
 * of the provided text via the `detectLanguage` method. 3. Correct the grammar
 * using `applyCorrections` based on detected issues. 4. Cache the results to
 * avoid redundant processing. 5. Clear cached data using `clearCache` when
 * necessary. Cache is used to store: - The corrected text (for grammar
 * checking). - The detected language (to avoid re-detecting the language every
 * time). Cache will be evicted either manually (via the `clearCache` method) or
 * automatically according to the configured expiration settings in the cache
 * configuration.
 */

@Service
@Slf4j
public class GrammarChecker implements GrammarCheckerService {
    private final JLanguageTool enLangTool;
    private final JLanguageTool uaLangTool;
    private final CacheManager cacheManager;

    public GrammarChecker(@Qualifier("englishTool") JLanguageTool enLangTool,
        @Qualifier("ukrainianTool") JLanguageTool uaLangTool,
        CacheManager cacheManager) {
        this.enLangTool = enLangTool;
        this.uaLangTool = uaLangTool;
        this.cacheManager = cacheManager;
    }

    /**
     * Checks and corrects the grammar of the provided text. This method uses the
     * configured grammar checking tool (JLanguageTool) to find and apply
     * corrections to grammar issues in the input text. The result is cached for
     * future use, so the same text does not need to be checked again if it has
     * already been processed.
     *
     * @param text the text to check and correct. This is the input text which will
     *             be processed for grammar errors.
     * @return the corrected text with grammar fixes applied. If no errors are
     *         found, the original text will be returned unchanged.
     * @throws IOException           if an error occurs during grammar checking.
     *                               This could happen if the grammar tool
     *                               encounters an I/O issue while processing the
     *                               text.
     * @throws GrammarCheckException if an error occurs during grammar checking.
     *                               This could happen due to unexpected exceptions
     *                               or issues in processing the grammar. This
     *                               method first checks if a cached version of the
     *                               result for the provided text is available in
     *                               the `languageCache`. If available, it returns
     *                               the cached value. If not, it proceeds with
     *                               grammar checking using the appropriate language
     *                               tool, applies the necessary corrections, caches
     *                               the result, and returns it.
     */
    @Cacheable(value = "languageCache", key = "#text")
    @Override
    public String checkGrammar(String text) throws IOException {
        try {
            String detectedLanguage = detectLanguage(text);
            JLanguageTool languageTool = getLanguageTool(detectedLanguage);

            List<RuleMatch> matches = languageTool.check(text);
            if (matches.isEmpty()) {
                return text;
            }
            return applyCorrections(text, matches);
        } catch (Exception e) {
            throw new GrammarCheckException(ERROR_GRAMMAR_CHECKING_MESSAGE, e);
        }
    }

    /**
     * Clears the cache for the provided text. This method removes the cached
     * grammar results and detected language for the specified text, both in the
     * `languageCache` and `grammarCache` caches. It can be used when the data for a
     * specific text needs to be invalidated, for example, after an update or change
     * in the text that might require re-checking the grammar or re-detecting the
     * language.
     *
     * @param text the text whose cache needs to be cleared. The cache entries
     *             associated with this text will be evicted from both
     *             `languageCache` and `grammarCache`. This method is typically
     *             called when a user explicitly requests the removal of cached data
     *             (e.g., after modifying or updating a text). The cache is cleared
     *             from both caches to ensure that subsequent requests for the same
     *             text will trigger a fresh evaluation of the grammar and language.
     */
    @Override
    @CacheEvict(value = {"languageCache", "grammarCache"}, key = "#text")
    public void clearCache(String text) {
        evictCache(LANGUAGE_CACHE_NAME, text);
        evictCache(GRAMMAR_CACHE_NAME, text);
    }

    /**
     * Detects the language of the provided text. This method uses OpenNLP's
     * language detection tools to determine the language of the provided text. The
     * detected language is cached to avoid re-detecting the language for the same
     * text in the future.
     *
     * @param text the text whose language needs to be detected. This text will be
     *             analyzed to determine its language.
     * @return the detected language code (e.g., "en" for English, "uk" for
     *         Ukrainian). This is the language code corresponding to the detected
     *         language of the input text.
     */
    private String detectLanguage(String text) {
        String cachedLanguageResult = getCacheValue(text);
        if (Objects.nonNull(cachedLanguageResult)) {
            return cachedLanguageResult;
        }

        try (InputStream modelIn = getClass().getResourceAsStream(LANG_DETECT_MODEL_PATH)) {
            if (Objects.isNull(modelIn)) {
                return DEFAULT_LANGUAGE_CODE;
            }
            LanguageDetectorModel model = new LanguageDetectorModel(modelIn);
            LanguageDetector localLanguageDetector = new LanguageDetectorME(model);
            Language bestLanguage = localLanguageDetector.predictLanguage(text);
            String detectedLanguage = bestLanguage.getLang();

            putCacheValue(text, detectedLanguage);
            return detectedLanguage;
        } catch (IOException e) {
            return DEFAULT_LANGUAGE_CODE;
        }
    }

    /**
     * Retrieves the appropriate JLanguageTool instance based on the detected
     * language. This method selects the correct grammar checking tool
     * (JLanguageTool) based on the provided language code. For English, it returns
     * the English language tool; for Ukrainian, it returns the Ukrainian language
     * tool.
     *
     * @param language the detected language code (e.g., "en" for English, "uk" for
     *                 Ukrainian).
     * @return the JLanguageTool instance for the detected language. This instance
     *         will be used to check the grammar of the text in the corresponding
     *         language. If the language code provided is "en", the English language
     *         tool is returned. If it is not "en", the Ukrainian language tool is
     *         returned by default. You can extend this logic if more languages are
     *         supported in the future.
     */
    private JLanguageTool getLanguageTool(String language) {
        return ENGLISH_PREFIX.equals(language) ? enLangTool : uaLangTool;
    }

    /**
     * Applies grammar corrections to the text based on the provided rule matches.
     * This method takes the text and a list of `RuleMatch` objects, which represent
     * the grammar issues identified by the grammar checking tool. It then applies
     * the suggested corrections to the text.
     *
     * @param text    the original text with potential grammar issues.
     * @param matches the list of `RuleMatch` objects, each representing a grammar
     *                issue found in the text. These contain the position of the
     *                error and suggested corrections.
     * @return the corrected text with all the suggested changes applied. If there
     *         are no grammar issues, the original text is returned unchanged. This
     *         method iterates over the list of `RuleMatch` objects, replacing the
     *         erroneous parts of the text with the suggested replacements. It takes
     *         care to adjust for changes in the text's length as corrections are
     *         applied, ensuring the correct positions are updated. The method
     *         returns the fully corrected text.
     */
    private String applyCorrections(String text, List<RuleMatch> matches) {
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

    /**
     * Retrieves the cached value for the given key. This method checks if a cache
     * exists for the provided key in the `languageCache`. If the cache is found, it
     * retrieves the cached value; otherwise, it returns null.
     *
     * @param key the cache key (text to be used for looking up the cache).
     * @return the cached value if found, or null if the key does not exist in the
     *         cache. The method ensures that only valid cache entries are
     *         retrieved. If the cache does not exist, it logs an error message and
     *         returns null. This prevents unnecessary errors in the flow when
     *         accessing the cache.
     */
    private String getCacheValue(String key) {
        Cache cache = cacheManager.getCache(LANGUAGE_CACHE_NAME);
        if (Objects.isNull(cache)) {
            return null;
        }
        return cache.get(key, String.class);
    }

    /**
     * Puts a value into the cache with the given key. This method stores the
     * provided value in the `languageCache` using the provided key. It ensures that
     * the cache is updated with the latest processed result for reuse.
     *
     * @param key   the cache key (text to be used as the cache key).
     * @param value the value to cache (processed text or detected language). If the
     *              cache is available, the value is stored under the provided key.
     *              If the cache is unavailable, the method does nothing, but the
     *              cache miss is logged for transparency.
     */
    private void putCacheValue(String key, String value) {
        Cache cache = cacheManager.getCache(LANGUAGE_CACHE_NAME);
        if (Objects.nonNull(cache)) {
            cache.put(key, value);
        }
    }

    /**
     * Evicts the cache entry for the given key. This method removes the cache entry
     * for the provided key from the specified cache. It is typically used when data
     * needs to be invalidated or refreshed (e.g., after a change in the text).
     *
     * @param cacheName the name of the cache (e.g., "languageCache" or
     *                  "grammarCache").
     * @param key       the cache key (text for which the cache entry will be
     *                  evicted). The method checks if the cache exists before
     *                  attempting to evict the entry. If the cache is not found, it
     *                  logs an error message for clarity.
     */
    private void evictCache(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (Objects.nonNull(cache)) {
            cache.evict(key);
        }
    }
}
