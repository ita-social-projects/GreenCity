package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.HabitStatus;
import java.time.ZonedDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/habit_status.sql")
class HabitStatusRepoTest {
    @Autowired
    HabitStatusRepo habitStatusRepo;

    @Test
    void findByHabitAssignIdTest() {
        HabitStatus habitStatus = habitStatusRepo.findByHabitAssignId(2L).get();
        assertEquals(2L,habitStatus.getId());
    }

    @Test
    void findByUserIdAndHabitIdTest() {
        HabitStatus habitStatus = habitStatusRepo.findByUserIdAndHabitId(2L,1L).get();
        assertEquals(2L,habitStatus.getId());
    }

    @Test
    void findByUserIdAndHabitIdAndCreateDate() {
        HabitStatus habitStatus = habitStatusRepo.findByUserIdAndHabitIdAndCreateDate(2L,1L,
            ZonedDateTime.parse("2020-09-10T03:00:00+00")).get();
        assertEquals(2L,habitStatus.getId());
    }

    @Test
    void deleteByHabitAssignIdTest() {
        habitStatusRepo.deleteByHabitAssignId(2L);
        Optional<HabitStatus> habitStatus = habitStatusRepo.findByHabitAssignId(2L);
        assertEquals(Optional.empty(),habitStatus);
    }

    @Test
    void deleteByUserIdAndHabitIdTest() {
        habitStatusRepo.deleteByUserIdAndHabitId(2L, 1L);
        Optional<HabitStatus> habitStatus =  habitStatusRepo.findByUserIdAndHabitId(2L,1L);
        assertEquals(Optional.empty(),habitStatus);
    }

    @Test
    void deleteByUserIdAndHabitIdAndCreateDateTest() {
        habitStatusRepo.deleteByUserIdAndHabitIdAndCreateDate(2L, 1L,
            ZonedDateTime.parse("2020-09-10T03:00:00+00"));
        Optional<HabitStatus> habitStatus =  habitStatusRepo.findByUserIdAndHabitId(2L,1L);
        assertEquals(Optional.empty(),habitStatus);
    }
}