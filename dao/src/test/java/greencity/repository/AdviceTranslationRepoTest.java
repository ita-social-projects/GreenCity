package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.Advice;
import greencity.entity.Habit;
import greencity.entity.Language;
import greencity.entity.Translation;
import greencity.entity.localization.AdviceTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
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

    private Advice advice = Advice.builder().id(1L)
            .habit(Habit.builder().id(1L).image("image_one.png")
                    .build())
            .build();

    private AdviceTranslation firstAdviceTranslation = AdviceTranslation.builder()
            .id(1L)
            .language(Language.builder().id(1L).code("uk").build())
            .content("Привіт")
            .advice(advice).build();

    private AdviceTranslation secondAdviceTranslation = AdviceTranslation.builder()
            .id(2L)
            .language(Language.builder().id(2L).code("en").build())
            .content("Hello")
            .advice(Advice.builder().id(2L)
                    .habit(Habit.builder().id(2L).image("image_two.png")
                    .build())
            .build()).build();

    private AdviceTranslation thirdAdviceTranslation = AdviceTranslation.builder()
            .id(3L)
            .language(Language.builder().id(3L).code("ru").build())
            .content("Привет")
            .advice(Advice.builder().id(3L)
                    .habit(Habit.builder().id(3L).image("image_three.png")
                            .build())
                    .build()).build();

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
        List<AdviceTranslation> actual = List.of(firstAdviceTranslation, secondAdviceTranslation,
                thirdAdviceTranslation);
        List<AdviceTranslation> expected = adviceTranslationRepo.findAll();

        List<Long> actualIds = actual.stream().map(Translation::getId).collect(Collectors.toList());
        List<Long> expectedIds = expected.stream().map(Translation::getId).collect(Collectors.toList());

        assertEquals(expected.size(), 3);
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void deleteAllByAdvice() {
        adviceTranslationRepo.deleteAllByAdvice(advice);

        List<AdviceTranslation> actual = List.of(secondAdviceTranslation, thirdAdviceTranslation);
        List<AdviceTranslation> expected = adviceTranslationRepo.findAll();

        List<Long> actualIds = actual.stream().map(Translation::getId).collect(Collectors.toList());
        List<Long> expectedIds = expected.stream().map(Translation::getId).collect(Collectors.toList());

        assertEquals(expected.size(), 2);
        assertEquals(expectedIds, actualIds);
    }
}