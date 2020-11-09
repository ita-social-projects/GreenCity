package greencity.repository;

import greencity.DaoApplication;
import greencity.utils.ModelUtils;
import greencity.entity.Advice;
import greencity.entity.Translation;
import greencity.entity.localization.AdviceTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<AdviceTranslation> actual = Optional.empty();
        Optional<AdviceTranslation> expected = adviceTranslationRepo
            .getRandomAdviceTranslationByHabitIdAndLanguage(languageCode, habitId);

        assertEquals(expected, actual);
    }

    @Test
    void findAdviceTranslationByLanguageCodeAndContent() {
        String languageCode = "en";
        String content = "Hello";
        AdviceTranslation expected = adviceTranslationRepo
            .findAdviceTranslationByLanguageCodeAndContent(languageCode, content).get();

        assertEquals(2L, expected.getId());
        assertEquals(2L, expected.getAdvice().getId());
        assertEquals(2L, expected.getLanguage().getId());
    }

    @Test
    void findAdviceTranslationByLanguageCodeAndContentNotFound() {
        String languageCode = "en";
        String content = "World";
        Optional<AdviceTranslation> actual = Optional.empty();
        Optional<AdviceTranslation> expected = adviceTranslationRepo
            .findAdviceTranslationByLanguageCodeAndContent(languageCode, content);

        assertEquals(expected, actual);
    }

    @Test
    void findAll() {
        List<AdviceTranslation> actual = ModelUtils.getAdviceTranslations();
        List<AdviceTranslation> expected = adviceTranslationRepo.findAll();

        List<Long> actualIds = actual.stream().map(Translation::getId).collect(Collectors.toList());
        List<Long> expectedIds = expected.stream().map(Translation::getId).collect(Collectors.toList());

        assertEquals(3, expected.size());
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void deleteAllByAdvice() {
        Advice advice = ModelUtils.getAdvice();
        AdviceTranslation secondAdviceTranslation = ModelUtils.getAdviceTranslationSecond();
        AdviceTranslation thirdAdviceTranslation = ModelUtils.getAdviceTranslationThird();
        adviceTranslationRepo.deleteAllByAdvice(advice);

        List<AdviceTranslation> actual = List.of(secondAdviceTranslation, thirdAdviceTranslation);
        List<AdviceTranslation> expected = adviceTranslationRepo.findAll();

        List<Long> actualIds = actual.stream().map(Translation::getId).collect(Collectors.toList());
        List<Long> expectedIds = expected.stream().map(Translation::getId).collect(Collectors.toList());

        assertEquals(2, expected.size());
        assertEquals(expectedIds, actualIds);
    }
}
