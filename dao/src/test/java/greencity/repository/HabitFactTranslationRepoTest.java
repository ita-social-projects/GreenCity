package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/habit_fact_translation.sql")
class HabitFactTranslationRepoTest {
    @Autowired
    private HabitFactTranslationRepo habitFactTranslationRepo;

    @Test
    void getRandomHabitFactTranslationByHabitIdAndLanguageTest_shouldReturnCorrectValue() {
        HabitFactTranslation res = habitFactTranslationRepo
            .getRandomHabitFactTranslationByHabitIdAndLanguage("en",2L).get();
        assertEquals("testFactTranslationsContent",res.getContent());
    }

    @Test
    void getRandomHabitFactTranslationByHabitIdAndLanguageTest__shouldReturnEmptyOptional() {
        Optional<HabitFactTranslation> res = habitFactTranslationRepo
            .getRandomHabitFactTranslationByHabitIdAndLanguage("en",1L);
        assertEquals(Optional.empty(),res);
    }

    @Test
    void findFactTranslationByLanguage_CodeAndHabitFactTest() {
        HabitFact habitFact = HabitFact.builder().id(2L).build();
        HabitFactTranslation res = habitFactTranslationRepo
            .findFactTranslationByLanguage_CodeAndContent("en","testFactTranslationsContent").get();
        assertEquals("en",res.getLanguage().getCode());
    }

    @Test
    void findRandomHabitFactTest() {
        HabitFactTranslation res = habitFactTranslationRepo.findRandomHabitFact().get(0);
        assertNotEquals(null,res);
    }

    @Test
    void findAllByFactOfDayStatusAndLanguageIdTest() {

    }
}