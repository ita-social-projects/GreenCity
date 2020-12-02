package greencity.repository;

import static org.junit.jupiter.api.Assertions.*;

import greencity.DaoApplication;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/habit_statistic_and_translation.sql")
class HabitTranslationRepoTest {

    @Autowired
    private HabitTranslationRepo habitTranslationRepo;

    @Test
    void findByNameAndLanguageCodeTest_shouldReturnCorrectTranslation() {
        HabitTranslation habitTranslation = habitTranslationRepo
            .findByNameAndLanguageCode("Save bags", "en").get();

        assertAll(() -> assertEquals("bag description", habitTranslation.getDescription()),
            () -> assertEquals("bags", habitTranslation.getHabitItem()));
    }

    @Test
    void findByNameAndLanguageCodeTest_shouldReturnEmptyOptionalWhenWrongId() {
        Optional<HabitTranslation> habitTranslation = habitTranslationRepo
            .findByNameAndLanguageCode("Экономить пакеты", "ua");

        assertTrue(habitTranslation.isEmpty());
    }

    @Test
    void findByHabitAndLanguageCodeTest_shouldReturnCorrectTranslation() {
        Habit habit = Habit.builder().id(1L).build();
        HabitTranslation habitTranslation = habitTranslationRepo
            .findByHabitAndLanguageCode(habit, "en").get();

        assertAll(() -> assertEquals("bag description", habitTranslation.getDescription()),
            () -> assertEquals("bags", habitTranslation.getHabitItem()));
    }

    @Test
    void findByHabitAndLanguageCodeTest_shouldReturnEmptyOptionalWhenWrongId() {
        Habit habit = Habit.builder().id(3L).build();
        Optional<HabitTranslation> habitTranslation = habitTranslationRepo
            .findByHabitAndLanguageCode(habit, "ua");

        assertTrue(habitTranslation.isEmpty());
    }

    @Test
    void findHabitTranslationsByUserAndAcquiredStatusTest_shouldReturnCorrectTranslationsList() {
        List<HabitTranslation> habitTranslations = habitTranslationRepo
            .findHabitTranslationsByUserAndAcquiredStatus(1L, "en");

        assertEquals(1, habitTranslations.size());
        assertEquals("bags", habitTranslations.get(0).getHabitItem());
    }

    @Test
    void findHabitTranslationsByUserAndAcquiredStatusTest_shouldReturnEmptyTranslationsListWhenWrongId() {
        List<HabitTranslation> habitTranslations = habitTranslationRepo
            .findHabitTranslationsByUserAndAcquiredStatus(8L, "ua");

        assertTrue(habitTranslations.isEmpty());
    }

    @Test
    void findAllByLanguageCodeTest_shouldReturnCorrectTranslationsList() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<HabitTranslation> habitTranslations = habitTranslationRepo
            .findAllByLanguageCode(pageRequest, "en").getContent();

        HabitTranslation actual = habitTranslations.get(1);

        assertEquals(2, habitTranslations.size());
        assertEquals("cap description", actual.getDescription());
    }

    @Test
    void findAllByLanguageCodeTest_shouldReturnEmptyTranslationsListWhenWrongLanguageCode() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<HabitTranslation> habitTranslations = habitTranslationRepo
            .findAllByLanguageCode(pageRequest, "uk").getContent();

        assertTrue(habitTranslations.isEmpty());
    }
}
