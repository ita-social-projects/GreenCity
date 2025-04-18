package greencity.utils;

import static greencity.constant.LanguageServiceConstants.*;
import greencity.enums.Language;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for resolving language preferences based on the `Accept-Language` HTTP header.
 * This class provides a static method to parse the language code and map it to a supported language.
 *
 * <p>Supported languages are defined in the {@link greencity.enums.Language} enum.
 * If the language code is not recognized, it defaults to English.</p>
 *
 * <p>This class is designed to be used statically and cannot be instantiated.</p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LanguageResolverUtil {
    /**
     * Parses the `Accept-Language` header and resolves it to a supported language.
     *
     * @param acceptLanguage the `Accept-Language` header value from the HTTP request.
     * @return the display name of the resolved language, as defined in {@link greencity.enums.Language}.
     *         Defaults to English if the language code is not recognized or the header is null/blank.
     */
    public static String parseLanguage(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isBlank()) {
            return Language.ENGLISH.getDisplayName();
        }

        String lang = acceptLanguage
            .split(COMMA)[0]
            .split(DASH)[0]
            .trim()
            .toLowerCase();

        return Language.fromCode(lang).getDisplayName();
    }
}
