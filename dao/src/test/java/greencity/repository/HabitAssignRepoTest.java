package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.EcoNews;
import greencity.entity.HabitAssign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/habit_assign.sql")
class HabitAssignRepoTest {

    @Autowired
    private HabitAssignRepo habitAssignRepo;

    @Test
    void findByHabitIdAndUserIdAndStatusIsCancelledTest() {
        Long expected = 2L;
        Long actual = habitAssignRepo.findByHabitIdAndUserIdAndStatusIsCancelled(2L, 2L).getId();
        assertEquals(expected, actual);
    }

    @Test
    void findAllByUserIdAndStatusAcquiredTest() {
        List<HabitAssign> habitAssignList = habitAssignRepo.findAllByUserIdAndStatusAcquired(3L);
        assertEquals(1L, habitAssignList.size());
        assertEquals(4L, habitAssignList.get(0).getId());
    }
}