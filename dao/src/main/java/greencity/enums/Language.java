package greencity.enums;

import static greencity.constant.LanguageServiceConstants.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing the supported languages in the application.
 *
 * <p>
 * Each language is defined with the following attributes:
 * </p>
 * <ul>
 * <li><b>code</b>: The language code (e.g., "en" for English, "ua" for
 * Ukrainian).</li>
 * <li><b>displayName</b>: The human-readable name of the language.</li>
 * <li><b>formattedDisplayName</b>: A formatted version of the display
 * name.</li>
 * </ul>
 *
 * <p>
 * The enum also provides a utility method to resolve a language from its code.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum Language {
    ENGLISH(
            ENGLISH_CODE,
            ENGLISH_DISPLAY_NAME,
            ENGLISH_FORMATTED_DISPLAY_NAME,
            ENGLISH_ALTERNATIVE_DISPLAY_NAME),
    UKRAINIAN(
              UKRAINIAN_CODE,
              UKRAINIAN_DISPLAY_NAME,
              UKRAINIAN_FORMATTED_DISPLAY_NAME,
              UKRAINIAN_ALTERNATIVE_DISPLAY_NAME);

    private final String code;
    private final String displayName;
    private final String formattedDisplayName;
    private final String alternativeDisplayName;

    /**
     * Resolves a {@link Language} enum value from the given language code.
     *
     * @param code the language code to resolve.
     * @return the corresponding {@link Language} enum value, or
     *         {@link Language#ENGLISH} if the code is not recognized.
     */
    public static Language fromCode(String code) {
        for (Language language : values()) {
            if (language.code.equalsIgnoreCase(code)) {
                return language;
            }
        }
        return ENGLISH;
    }
}
