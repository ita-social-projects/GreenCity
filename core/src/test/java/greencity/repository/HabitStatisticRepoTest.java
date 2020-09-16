package greencity.repository;

import java.util.Date;
import java.util.List;
import javax.persistence.Tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HabitStatisticRepoTest {

    @Autowired
    private HabitStatisticRepo habitStatisticRepo;

    @Test
    public void emptyDataSourceTest() {
        List<Tuple> amountOfAllHabitItems =
            habitStatisticRepo.getStatisticsForAllHabitItemsByDate(new Date(), "en");
        assertTrue(amountOfAllHabitItems.isEmpty());
    }

    @Test
    @Sql("file:src/test/resources/sql/single_habit_statistic.sql")
    public void singleHabitStatisticTest() {
        List<Tuple> amountOfAllHabitItems =
            habitStatisticRepo.getStatisticsForAllHabitItemsByDate(new Date(), "en");
        assertEquals(1, amountOfAllHabitItems.size());
        Tuple tuple = amountOfAllHabitItems.get(0);
        assertEquals("foo", tuple.get(0));
        assertEquals(42L, tuple.get(1));
    }

    @Test
    @Sql("file:src/test/resources/sql/most_popular_habit_statistic.sql")
    public void mostPopularHabitStatisticTest() {
        List<Tuple> amountOfAllHabitItems =
            habitStatisticRepo.getStatisticsForAllHabitItemsByDate(new Date(), "en");
        assertEquals(2, amountOfAllHabitItems.size());
        Tuple mostPopularHabitTuple = amountOfAllHabitItems.get(0);
        assertEquals("baz", mostPopularHabitTuple.get(0));
        assertEquals(45L, mostPopularHabitTuple.get(1));
        Tuple secondByPopularityHabitTuple = amountOfAllHabitItems.get(1);
        assertEquals("eggs", secondByPopularityHabitTuple.get(0));
        assertEquals(8L, secondByPopularityHabitTuple.get(1));
    }

    @Test
    @Sql("file:src/test/resources/sql/disabled_habit_statistics.sql")
    public void habitsWithDisabledMostPopularHabitTest() {
        List<Tuple> amountOfAllHabitItems =
            habitStatisticRepo.getStatisticsForAllHabitItemsByDate(new Date(), "en");
        assertEquals(1, amountOfAllHabitItems.size());
        Tuple tuple = amountOfAllHabitItems.get(0);
        assertEquals("eggs", tuple.get(0));
        assertEquals(8L, tuple.get(1));
    }

    @Test
    @Sql("file:src/test/resources/sql/outdated_habits_statistic.sql")
    public void habitsWithOutdatedMostPopularHabitTest() {
        List<Tuple> amountOfAllHabitItems
            = habitStatisticRepo.getStatisticsForAllHabitItemsByDate(new Date(), "en");
        assertEquals(2, amountOfAllHabitItems.size());
        Tuple mostPopularHabitTuple = amountOfAllHabitItems.get(0);
        assertEquals("baz", mostPopularHabitTuple.get(0));
        assertEquals(3L, mostPopularHabitTuple.get(1));
        Tuple secondByPopularityHabitTuple = amountOfAllHabitItems.get(1);
        assertEquals("eggs", secondByPopularityHabitTuple.get(0));
        assertEquals(8L, secondByPopularityHabitTuple.get(1));
    }

    @Test
    @Sql("file:src/test/resources/sql/habit_statistics_id_not_match_habit_id.sql")
    public void habitStatisticIdDoesNotMatchHabitId() {
        List<Tuple> amountOfAllHabitItems
            = habitStatisticRepo.getStatisticsForAllHabitItemsByDate(new Date(), "en");
        assertEquals(1, amountOfAllHabitItems.size());
        Tuple mostPopularHabitTuple = amountOfAllHabitItems.get(0);
        assertEquals("baz", mostPopularHabitTuple.get(0));
        assertEquals(42L, mostPopularHabitTuple.get(1));
    }
}
