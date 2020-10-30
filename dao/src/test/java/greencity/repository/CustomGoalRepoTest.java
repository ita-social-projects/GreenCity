package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.CustomGoal;
import greencity.enums.GoalStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/custom_goal.sql")
class CustomGoalRepoTest {
    @Autowired
    private CustomGoalRepo customGoalRepo;

    @Test
    void findAllAvailableCustomGoalsTest() {
        List<CustomGoal> customGoals = customGoalRepo.findAllAvailableCustomGoalsForUserId(1L);
        assertEquals(3, customGoals.size());
    }

    @Test
    void findByUserIdTest() {
        CustomGoal customGoal = customGoalRepo.findByUserId(2L);
        assertEquals(6L, customGoal.getId());
    }

    @Test
    void findAllTest() {
        List<CustomGoal> customGoals = customGoalRepo.findAllByUserId(1L);
        assertEquals(5, customGoals.size());
    }
}
