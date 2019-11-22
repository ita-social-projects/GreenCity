package greencity.repository;

import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link HabitStatistic} entity.
 */
@Repository
public interface HabitStatisticRepo extends JpaRepository<HabitStatistic, Long>,
    JpaSpecificationExecutor<HabitStatistic> {
    /**
     * Method for finding statistic for current date.
     *
     *
     * @return {@link HabitStatistic} instance, if it doesn't exist returns Optional.
     */
    @Query(value = "SELECT hs FROM HabitStatistic hs WHERE hs.createdOn=:localDate AND habit_id=:habitId")
    Optional<HabitStatistic> findHabitStatByDate(@Param("localDate") LocalDate localDate,
                                                 @Param("habitId") Long habitId);

    /**
     * Method for finding sum of all untaken items per month.
     *
     * @param habitId {@link Habit} id.
     * @return sum of items per month.
     */
    @Query(value = "SELECT SUM(hs.amountOfItems) FROM HabitStatistic hs WHERE habit_id=:habitId"
        + " AND MONTH(createdOn) = MONTH(CURRENT_DATE())\n"
        + " AND YEAR(createdOn) = YEAR(CURRENT_DATE())")
    Optional<Integer> getSumOfAllItems(@Param("habitId") Long habitId);

    /**
     * Method for finding all {@link HabitStatisticDto} by {@link Habit id}.
     *
     * @param habitId {@link Habit} id.
     * @return list of {@link HabitStatisticDto} instances.
     */
    List<HabitStatistic> findAllByHabitId(Long habitId);

    /**
     * Method for finding amount of items for the same day as now but in
     * previous month.
     *
     * @param habitId {@link Habit} id.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(nativeQuery = true, value = "SELECT amount_of_items FROM habit_statistics\n"
        + " WHERE habit_statistics.date = CURRENT_DATE - INTERVAL 1 MONTH AND habit_id =:habitId")
    Optional<Integer> getAmountOfItemsInPreviousMonth(@Param("habitId") Long habitId);

    /**
     * Method for finding amount of items for the current day.
     *
     * @param habitId {@link Habit} id.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(nativeQuery = true, value = "SELECT amount_of_items FROM habit_statistics\n"
        + " WHERE habit_statistics.date = CURRENT_DATE AND habit_id =:habitId")
    Optional<Integer> getAmountOfItemsToday(@Param("habitId") Long habitId);
}