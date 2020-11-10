package greencity.repository;

import greencity.containers.TestPostresConfig;
import greencity.entity.HabitStatus;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(classes = TestPostresConfig.class)

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(value = {
    "classpath:posgresql-test.properties"})
@Sql("classpath:sql/habit_status.sql")
class HabitStatusRepoTest {
    @Autowired
    HabitStatusRepo habitStatusRepo;

    @Test
    void findByHabitAssignIdTest() {
        HabitStatus habitStatus = habitStatusRepo.findByHabitAssignId(2L).get();
        assertEquals(2L, habitStatus.getId());
    }

    @Test
    void findByUserIdAndHabitIdTest() {
        HabitStatus habitStatus = habitStatusRepo.findByHabitIdAndUserId(1L, 2L).get();
        assertEquals(2L, habitStatus.getId());
    }
}
