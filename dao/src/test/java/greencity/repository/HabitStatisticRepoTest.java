/*
package greencity.repository;

import greencity.DaoApplication;
import greencity.entity.HabitStatistic;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
class HabitStatisticRepoTest {
    @Autowired
    private HabitStatisticRepo habitStatisticRepo;

    @Test
    void emptyDataSourceTest() {
        List<Tuple> amountOfAllHabitItems =
            habitStatisticRepo.getStatisticsForAllHabitItemsByDate(ZonedDateTime.now(), "en");
        assertTrue(amountOfAllHabitItems.isEmpty());
    }

    @Test
    @Sql("file:src/test/resources/sql/single_habit_statistic.sql")
    void singleHabitStatisticTest() {
        List<HabitStatistic> allByHabitId = habitStatisticRepo.findAllByHabitId(1L);
        List<Tuple> amountOfAllHabitItems =
            habitStatisticRepo.getStatisticsForAllHabitItemsByDate(ZonedDateTime.now(), "en");
        assertEquals(1, amountOfAllHabitItems.size());
        Tuple tuple = amountOfAllHabitItems.get(0);
        assertEquals("foo", tuple.get(0));
        assertEquals(42L, tuple.get(1));
    }

    @Test
    @Sql("file:src/test/resources/sql/most_popular_habit_statistic.sql")
    void mostPopularHabitStatisticTest() {
        List<Tuple> amountOfAllHabitItems =
            habitStatisticRepo.getStatisticsForAllHabitItemsByDate(ZonedDateTime.now(), "en");
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
    void habitsWithDisabledMostPopularHabitTest() {
        List<Tuple> amountOfAllHabitItems =
            habitStatisticRepo.getStatisticsForAllHabitItemsByDate(ZonedDateTime.now(), "en");
        assertEquals(1, amountOfAllHabitItems.size());
        Tuple tuple = amountOfAllHabitItems.get(0);
        assertEquals("eggs", tuple.get(0));
        assertEquals(8L, tuple.get(1));
    }

    @Test
    @Sql("file:src/test/resources/sql/outdated_habits_statistic.sql")
    void habitsWithOutdatedMostPopularHabitTest() {
        List<Tuple> amountOfAllHabitItems
            = habitStatisticRepo.getStatisticsForAllHabitItemsByDate(ZonedDateTime.now(), "en");
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
    void habitStatisticIdDoesNotMatchHabitId() {
        List<Tuple> amountOfAllHabitItems
            = habitStatisticRepo.getStatisticsForAllHabitItemsByDate(ZonedDateTime.now(), "en");
        assertEquals(1, amountOfAllHabitItems.size());
        Tuple mostPopularHabitTuple = amountOfAllHabitItems.get(0);
        assertEquals("baz", mostPopularHabitTuple.get(0));
        assertEquals(42L, mostPopularHabitTuple.get(1));
    }
}
*/
