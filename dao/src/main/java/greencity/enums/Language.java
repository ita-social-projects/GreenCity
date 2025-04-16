package greencity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    ENGLISH("en", "English", "english"),
    UKRAINIAN("ua", "українська", "Українська");

    private final String code;
    private final String displayName;
    private final String formattedDisplayName;

    public static Language fromCode(String code) {
        for (Language language : values()) {
            if (language.code.equalsIgnoreCase(code)) {
                return language;
            }
        }
        return ENGLISH;
    }

}
