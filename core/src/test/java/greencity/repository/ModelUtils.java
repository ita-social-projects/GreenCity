package greencity.repository;

import greencity.entity.Language;

import java.util.ArrayList;
import java.util.List;

public class ModelUtils {
    public static Language getLanguage() {
        return Language.builder()
            .id(1L)
            .code("ua")
            .build();
    }

    public static List<String> getAllLanguages() {
        List<String> languages = new ArrayList<>();
        languages.add("ua");
        languages.add("en");
        languages.add("ru");
        return languages;
    }
}
