package greencity.repository;

import greencity.GreenCityApplication;
import greencity.IntegrationTestBase;
import greencity.entity.Language;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

//@ExtendWith(SpringExtension.class)
@DataMongoTest(includeFilters = @ComponentScan.Filter(Component.class))
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest//(classes = GreenCityApplication.class)
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
