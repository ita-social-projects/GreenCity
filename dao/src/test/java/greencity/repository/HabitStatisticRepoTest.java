package greencity.repository;

import static org.junit.jupiter.api.Assertions.*;

import greencity.DaoApplication;
import greencity.entity.HabitStatistic;
import greencity.enums.HabitRate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.persistence.Tuple;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoApplication.class)
@Sql("classpath:sql/habit_statistic_and_translation.sql")
class HabitStatisticRepoTest {

    @Autowired
    private HabitStatisticRepo habitStatisticRepo;

    @Test
    void findHabitAssignStatByDateTest_shouldReturnCorrectValue() {
        HabitStatistic habitStatistic = habitStatisticRepo
            .findStatByDateAndId(ZonedDateTime.parse("2020-10-09T03:00:00+03"), 1L).get();

        assertEquals(5, habitStatistic.getAmountOfItems());
        assertEquals(HabitRate.GOOD, habitStatistic.getHabitRate());
    }

    @Test
    void findHabitAssignStatByDateTest_shouldReturnEmptyOptional() {
        Optional<HabitStatistic> habitStatistic = habitStatisticRepo
            .findStatByDateAndId(ZonedDateTime.parse("2020-09-09T00:00:00+03"), 1L);

        assertFalse(habitStatistic.isPresent());
    }

    @Test
    void getSumOfAllItemsPerMonthTest_shouldReturnCorrectSumOfAllItems() {
        Integer sum = habitStatisticRepo
            .getSumOfAllItemsPerMonth(1L, ZonedDateTime.parse("2020-10-01T00:00:00+00")).get();

        assertEquals(9, sum);
    }

    @Test
    void getSumOfAllItemsPerMonthTest_shouldReturnEmptyOptional() {
        Optional<Integer> sum = habitStatisticRepo
            .getSumOfAllItemsPerMonth(1L, ZonedDateTime.parse("2020-10-20T00:00:00+00"));

        assertTrue(sum.isEmpty());
    }

    @Test
    void findAllByHabitIdTest_shouldReturnAllHabitStatistic() {
        List<HabitStatistic> habitStatistics = habitStatisticRepo.findAllByHabitId(3L);

        assertEquals(1, habitStatistics.size());
    }

    @Test
    void findAllByHabitIdTest_shouldReturnEmptyHabitStatistic() {
        List<HabitStatistic> habitStatistics = habitStatisticRepo.findAllByHabitId(7L);

        assertTrue(habitStatistics.isEmpty());
    }

    @Test
    void getAmountOfItemsOfAssignedHabitInPreviousDay_shouldReturnCorrectAmount() {
        Integer amount = habitStatisticRepo.getAmountOfItemsOfAssignedHabitInPreviousDay(5L).get();

        assertEquals(4, amount);
    }

    @Test
    void getAmountOfItemsOfAssignedHabitInPreviousDay_shouldReturnEmptyOptional() {
        Optional<Integer> amount = habitStatisticRepo.getAmountOfItemsOfAssignedHabitInPreviousDay(4L);

        assertTrue(amount.isEmpty());
    }

    @Test
    void getGeneralAmountOfHabitItemsInPreviousDayTest_shouldReturnCorrectGeneralAmount() {
        Integer amount = habitStatisticRepo.getGeneralAmountOfHabitItemsInPreviousDay(5L).get();

        assertEquals(4, amount);
    }

    @Test
    void getGeneralAmountOfHabitItemsInPreviousDayTest_shouldReturnEmptyOptional() {
        Optional<Integer> amount = habitStatisticRepo.getGeneralAmountOfHabitItemsInPreviousDay(6L);

        assertTrue(amount.isEmpty());
    }

    @Test
    void getAmountOfItemsOfAssignedHabitTodayTest_shouldReturnCorrectAmount() {
        Integer amount = habitStatisticRepo.getAmountOfItemsOfAssignedHabitToday(6L).get();

        assertEquals(3, amount);
    }

    @Test
    void getAmountOfItemsOfAssignedHabitTodayTest_shouldReturnEmptyOptional() {
        Optional<Integer> amount = habitStatisticRepo.getAmountOfItemsOfAssignedHabitToday(5L);

        assertFalse(amount.isPresent());
    }

    @Test
    void getGeneralAmountOfHabitItemsTodayTest_shouldReturnCorrectGeneralAmount() {
        Integer amount = habitStatisticRepo.getGeneralAmountOfHabitItemsToday(6L).get();

        assertEquals(3, amount);
    }

    @Test
    void getGeneralAmountOfHabitItemsTodayTest_shouldReturnEmptyOptional() {
        Optional<Integer> amount = habitStatisticRepo.getGeneralAmountOfHabitItemsToday(5L);

        assertFalse(amount.isPresent());
    }

    @Test
    void getStatisticsForAllHabitItemsByDate_shouldReturnCorrectStatistic() {
        List<Tuple> list = habitStatisticRepo
            .getStatisticsForAllHabitItemsByDate(ZonedDateTime.parse("2020-10-10T00:00:00+03"), "en");

        assertEquals(1, list.size());
    }

    @Test
    void getStatisticsForAllHabitItemsByDate_shouldReturnEmptyList() {
        List<Tuple> list = habitStatisticRepo
            .getStatisticsForAllHabitItemsByDate(ZonedDateTime.parse("2020-09-10T00:00:00+03"), "en");

        assertTrue(list.isEmpty());
    }
}
