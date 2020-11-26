package greencity.repository;

import greencity.entity.Goal;
import greencity.entity.UserGoal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("classpath:sql/user_goal.sql")
class UserGoalRepoTest {

    @Autowired
    UserGoalRepo userGoalRepo;

    @Test
    void findGoalByUserGoalIdTest() {
        Goal goal = userGoalRepo.findGoalByUserGoalId(1L).get();
        assertEquals(1, goal.getId());
    }
}
