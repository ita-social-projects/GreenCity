package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.enums.FactOfDayStatus;
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
    void getRandomHabitFactTranslationByHabitIdAndLanguageTest_shouldReturnEmptyOptional() {
        Optional<HabitFactTranslation> res = habitFactTranslationRepo
            .getRandomHabitFactTranslationByHabitIdAndLanguage("en",1L);
        assertEquals(Optional.empty(),res);
    }

    @Test
    void findFactTranslationByLanguage_CodeAndHabitFactTest_shouldReturnCorrectValue() {
        HabitFact habitFact = HabitFact.builder().id(2L).build();
        HabitFactTranslation res = habitFactTranslationRepo
            .findFactTranslationByLanguage_CodeAndContent("en","testFactTranslationsContent").get();
        assertEquals("en",res.getLanguage().getCode());
    }

    @Test
    void findRandomHabitFactTest_shouldReturnCorrectValue() {
        Integer res = habitFactTranslationRepo.findRandomHabitFact().size();
        assertEquals(1,res);
    }

    @Test
    void findAllByFactOfDayStatusAndLanguageIdTest_shouldReturnCorrectValue() {
        HabitFactTranslation res = habitFactTranslationRepo
            .findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus.USED,3L).get(0);
        assertEquals("Тест факт",res.getContent());
    }


    @Test
    void findAllByFactOfDayStatusAndLanguageIdTest_shouldReturnAllCorrectValues() {
        Integer res = habitFactTranslationRepo
            .findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus.POTENTIAL,2L).size();
        assertEquals(2,res);
    }

    @Test
    void updateFactOfDayStatusTest_shouldUpdateCorrectValue() {
        habitFactTranslationRepo.updateFactOfDayStatus(FactOfDayStatus.USED,FactOfDayStatus.CURRENT);
        HabitFactTranslation res = habitFactTranslationRepo
            .findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus.CURRENT,3L).get(0);
        assertEquals("Тест факт",res.getContent());
    }

    @Test
    void updateFactOfDayStatusTest_shouldUpdateMultiCorrectValues() {
        habitFactTranslationRepo.updateFactOfDayStatus(FactOfDayStatus.POTENTIAL,FactOfDayStatus.CURRENT);
        Integer res = habitFactTranslationRepo
            .findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus.CURRENT,2L).size();
        assertEquals(2,res);
        habitFactTranslationRepo.updateFactOfDayStatus(FactOfDayStatus.CURRENT,FactOfDayStatus.POTENTIAL);
    }

    @Test
    void updateFactOfDayStatusByHabitFactIdTest_shouldReturnCorrectValue() {
        habitFactTranslationRepo.updateFactOfDayStatusByHabitFactId(FactOfDayStatus.CURRENT,2L);
        HabitFactTranslation res = habitFactTranslationRepo
            .findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus.CURRENT,2L).get(0);
        assertEquals("testFactTranslationsContent",res.getContent());
    }
}