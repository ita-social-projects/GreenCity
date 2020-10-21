package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.localization.AdviceTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/advice_translation.sql")
class AdviceTranslationRepoTest {
    @Autowired
    private AdviceTranslationRepo adviceTranslationRepo;

    @Test
    void getRandomAdviceTranslationByHabitIdAndLanguage() {
        Long habitId = 1L;
        String languageCode = "uk";
        AdviceTranslation adviceTranslation = adviceTranslationRepo
                .getRandomAdviceTranslationByHabitIdAndLanguage(languageCode, habitId).get();

        assertEquals(1L, adviceTranslation.getId());
        assertEquals(1L, adviceTranslation.getAdvice().getId());
        assertEquals(1L, adviceTranslation.getLanguage().getId());
        assertEquals("Привіт", adviceTranslation.getContent());
    }

    @Test
    void getRandomAdviceTranslationByHabitIdAndLanguageNotFound() {
        Long habitId = 1L;
        String languageCode = "en";
        Optional<AdviceTranslation> expected = Optional.empty();
        Optional<AdviceTranslation> actual = adviceTranslationRepo
                .getRandomAdviceTranslationByHabitIdAndLanguage(languageCode, habitId);

        assertEquals(expected, actual);
    }

    @Test
    void findAdviceTranslationByLanguageCodeAndAdvice() {
        String languageCode = "en";
        String content = "Hello";
        AdviceTranslation actual = adviceTranslationRepo
                .findAdviceTranslationByLanguageCodeAndContent(languageCode, content).get();

        assertEquals(2L, actual.getId());
        assertEquals(2L, actual.getAdvice().getId());
        assertEquals(2L, actual.getLanguage().getId());
    }

    @Test
    void findAdviceTranslationByLanguageCodeAndAdviceNotFound() {
        String languageCode = "en";
        String content = "World";
        Optional<AdviceTranslation> expected = Optional.empty();
        Optional<AdviceTranslation> actual = adviceTranslationRepo
                .findAdviceTranslationByLanguageCodeAndContent(languageCode, content);

        assertEquals(expected, actual);
    }
}