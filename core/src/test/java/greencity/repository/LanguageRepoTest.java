package greencity.repository;

import greencity.GreenCityApplication;
import greencity.IntegrationTestBase;
import greencity.entity.Language;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GreenCityApplication.class)
class LanguageRepoTest extends IntegrationTestBase {
    @Autowired
    private LanguageRepo languageRepo;

    @Test
    void findAllLanguageCodes() {
        List<String> language = ModelUtils.getAllLanguages();
        List<String> actual = languageRepo.findAllLanguageCodes();
        Assertions.assertEquals(language, actual);
    }

    @Test
    void findByCode() {
        Language language = ModelUtils.getLanguage();
        Optional<Language> actual = languageRepo.findByCode("ua");
        Assertions.assertEquals(language.getCode(), actual.get().getCode());
    }

    @Test
    void findByTagTranslationId() {
        Language language = ModelUtils.getLanguage();
        Optional<Language> actual = languageRepo.findByTagTranslationId(1L);
        Assertions.assertEquals(language.getCode(), actual.get().getCode());
    }
}
