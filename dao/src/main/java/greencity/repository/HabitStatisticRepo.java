package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.Tuple;
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
     * @return {@link HabitStatistic} instance, if it doesn't exist returns Optional.
     */
    @Query(value = "SELECT hs FROM HabitStatistic hs WHERE hs.createdOn=:localDate AND habit_id=:habitId")
    Optional<HabitStatistic> findHabitStatByDate(@Param("localDate") ZonedDateTime localDate,
                                                 @Param("habitId") Long habitId);

    /**
     * Method for finding the sum of all untaken items for current month.
     *
     * @param habitId  {@link Habit} id.
     * @param firstDay first day of current month.
     * @return sum of items per month.
     */
    @Query(value = "SELECT SUM(hs.amountOfItems) FROM HabitStatistic hs\n"
        + " WHERE hs.habit.id=:habitId AND DATE(hs.createdOn) <= CURRENT_DATE AND"
        + " DATE(hs.createdOn) >=:firstDayOfMonth")
    Optional<Integer> getSumOfAllItemsPerMonth(@Param("habitId") Long habitId,
                                               @Param("firstDayOfMonth") ZonedDateTime firstDay);

    /**
     * Method for finding all {@link HabitStatistic} by {@link Habit id}.
     *
     * @param habitId {@link Habit} id.
     * @return list of {@link HabitStatistic} instances.
     */
    List<HabitStatistic> findAllByHabitId(Long habitId);

    /**
     * Method for finding amount of items for the previous day.
     *
     * @param habitId {@link Habit} id.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(value = "SELECT hs.amountOfItems FROM HabitStatistic hs\n"
        + " WHERE DATE(hs.createdOn) = CURRENT_DATE - 1 AND habit_id =:habitId")
    Optional<Integer> getAmountOfItemsInPreviousDay(@Param("habitId") Long habitId);

    /**
     * Method for finding amount of items for the current day.
     *
     * @param habitId {@link Habit} id.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(value = "SELECT hs.amountOfItems FROM HabitStatistic hs\n"
        + " WHERE DATE(hs.createdOn) = CURRENT_DATE AND habit_id =:habitId")
    Optional<Integer> getAmountOfItemsToday(@Param("habitId") Long habitId);

    /**
     * Returns {@link Tuple} consisting of habit item name(like 'cup' or 'bag')
     * and amount of not taken items by a specific date(according to the method date parameter)
     * for all the habits existing in the system.
     * Selection is filtered by habit status which must be active(true).
     * Result is sorted in descending order, that is the most popular habit
     * will be the first in a list and the least popular will be the last.
     *
     * @param statisticCreationDate Statistic creation date.
     * @param languageCode Language code of habit items, for example, 'en'.
     * @return {@link List} of {@link Tuple}s that contain item names and not taken amount of that items.
     * @author Shevtsiv Rostyslav
     */
    @Query("SELECT habitDictTranslation.habitItem, SUM(habitStatistic.amountOfItems) "
        + "FROM HabitStatistic habitStatistic "
        + "        INNER JOIN Habit habit ON habitStatistic.habit.id = habit.id AND habit.statusHabit = TRUE "
        + "                AND year(habitStatistic.createdOn) = year(:statisticCreationDate) "
        + "                AND month(habitStatistic.createdOn) = month(:statisticCreationDate) "
        + "                AND day(habitStatistic.createdOn) = day(:statisticCreationDate) "
        + "        INNER JOIN HabitDictionaryTranslation habitDictTranslation "
        + "                ON habitDictTranslation.habitDictionary.id = habit.habitDictionary.id "
        + "        INNER JOIN Language language ON habitDictTranslation.language.id = language.id "
        + "                AND language.code = :languageCode "
        + "GROUP BY habitDictTranslation.habitItem "
        + "ORDER BY COUNT(habit) DESC ")
    List<Tuple> getStatisticsForAllHabitItemsByDate(
        @Param("statisticCreationDate") ZonedDateTime statisticCreationDate,
        @Param("languageCode") String languageCode
    );

    /**
     * Method for getting amount of habits in progress by user id.
     *
     * @param id {@link Long} user id.
     * @return amount of habits in progress by user id.
     * @author Marian Datsko
     */
    @Query(nativeQuery = true,
        value = " SELECT COUNT(status) FROM public.habits AS h "
            + " JOIN public.habits_users_assign AS hus ON hus.habit_id = h.id "
            + " WHERE hus.users_id = :userId AND h.status = false ")
    Long getAmountOfHabitsInProgressByUserId(@Param("userId") Long id);

    /**
     * Method for getting amount of acquired habits by user id.
     *
     * @param id {@link Long} user id.
     * @return amount of acquired habits by user id.
     * @author Marian Datsko
     */
    @Query(nativeQuery = true,
        value = "SELECT COUNT(status) FROM public.habits AS h "
            + " JOIN public.habits_users_assign AS hus ON hus.habit_id = h.id "
            + " WHERE hus.users_id = :userId AND h.status = true")
    Long getAmountOfAcquiredHabitsByUserId(@Param("userId") Long id);
}
