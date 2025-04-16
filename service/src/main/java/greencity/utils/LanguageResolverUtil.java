package greencity.utils;

import static greencity.constant.LanguageServiceConstants.*;
import greencity.enums.Language;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LanguageResolverUtil {
    public static String parseLanguage(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isBlank()) {
            return Language.ENGLISH.getDisplayName();
        }

        String lang = acceptLanguage.split(COMMA)[0]
            .split(DASH)[0]
            .trim()
            .toLowerCase();

        return Language.fromCode(lang).getDisplayName();
    }
}
