package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.entity.User;
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
     * Method for finding {@link HabitStatistic} for certain
     * {@link java.time.LocalDate}.
     *
     * @return {@link HabitStatistic} instance, if it doesn't exist returns
     *         Optional.
     */
    @Query(value = "SELECT hs FROM HabitStatistic hs "
        + "WHERE cast(hs.createDate as date) = cast(:localDate as date) "
        + "AND hs.habitAssign.id = :habitAssignId")
    Optional<HabitStatistic> findStatByDateAndId(@Param("localDate") ZonedDateTime localDate,
        @Param("habitAssignId") Long habitAssignId);

    /**
     * Method for finding {@link HabitStatistic} for certain
     * {@link java.time.LocalDate} and {@link Habit} with {@link User} id's.
     *
     * @return {@link HabitStatistic} instance, if it doesn't exist returns
     *         Optional.
     */
    @Query(value = "SELECT hs FROM HabitStatistic hs "
        + "WHERE cast(hs.createDate as date) = cast(:localDate as date) "
        + "AND hs.habitAssign.habit.id = :habitId "
        + "AND hs.habitAssign.user.id = :userId ")
    Optional<HabitStatistic> findStatByDateAndHabitIdAndUserId(@Param("localDate") ZonedDateTime localDate,
        @Param("habitId") Long habitId,
        @Param("userId") Long userId);

    /**
     * Method for finding the sum of all untaken items of one {@link Habit} for
     * current month.
     *
     * @param habitId  {@link Habit} id.
     * @param firstDay first day of current month.
     * @return sum of items per month.
     */
    @Query(value = "SELECT SUM(hs.amountOfItems) FROM HabitStatistic hs "
        + "WHERE hs.habitAssign.habit.id = :habitId AND cast(hs.createDate as date) <= CURRENT_DATE "
        + "AND cast(hs.createDate as date) >= cast(:firstDayOfMonth as date)")
    Optional<Integer> getSumOfAllItemsPerMonth(@Param("habitId") Long habitId,
        @Param("firstDayOfMonth") ZonedDateTime firstDay);

    /**
     * Method for finding all {@link HabitStatistic} by {@link HabitAssign} id
     * (multiple statistics for one assigned habit for user).
     *
     * @param habitAssignId {@link Habit} id.
     * @return list of {@link HabitStatistic} instances.
     */
    List<HabitStatistic> findAllByHabitAssignId(Long habitAssignId);

    /**
     * Method for finding all {@link HabitStatistic} by {@link Habit} id (multiple
     * statistics for one habit in general).
     *
     * @param habitId {@link Habit} id.
     * @return list of {@link HabitStatistic} instances.
     */
    @Query(value = "SELECT hs FROM HabitStatistic hs "
        + "WHERE hs.habitAssign.habit.id = :habitId")
    List<HabitStatistic> findAllByHabitId(@Param("habitId") Long habitId);

    /**
     * Method for finding amount of items for one {@link HabitAssign} for previous
     * day.
     *
     * @param habitAssignId {@link HabitAssign} id.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(value = "SELECT hs.amountOfItems FROM HabitStatistic hs "
        + "WHERE cast(hs.createDate as date) = CURRENT_DATE - 1 "
        + "AND hs.habitAssign.id = :habitAssignId")
    Optional<Integer> getAmountOfItemsOfAssignedHabitInPreviousDay(@Param("habitAssignId") Long habitAssignId);

    /**
     * Method for finding general amount of certain {@link Habit} items for the
     * previous day.
     *
     * @param habitId {@link Habit} id.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(value = "SELECT hs.amountOfItems FROM HabitStatistic hs "
        + "WHERE cast(hs.createDate as date) = CURRENT_DATE - 1 AND hs.habitAssign.habit.id = :habitId")
    Optional<Integer> getGeneralAmountOfHabitItemsInPreviousDay(@Param("habitId") Long habitId);

    /**
     * Method for finding amount of items for one {@link HabitAssign} for current
     * day.
     *
     * @param habitAssignId {@link HabitAssign} id.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(value = "SELECT hs.amountOfItems FROM HabitStatistic hs "
        + "WHERE cast(hs.createDate as date) = CURRENT_DATE "
        + "AND hs.habitAssign.id = :habitAssignId")
    Optional<Integer> getAmountOfItemsOfAssignedHabitToday(@Param("habitAssignId") Long habitAssignId);

    /**
     * Method for finding general amount of {@link Habit} items for the current day.
     *
     * @param habitId {@link Habit} id.
     * @return amount of items in Optional in case of absence such info.
     */
    @Query(value = "SELECT hs.amountOfItems FROM HabitStatistic hs "
        + "WHERE cast(hs.createDate as date) = CURRENT_DATE AND hs.habitAssign.habit.id = :habitId")
    Optional<Integer> getGeneralAmountOfHabitItemsToday(@Param("habitId") Long habitId);

    /**
     * Returns {@link Tuple} consisting of habit item name(like 'cup' or 'bag') and
     * amount of not taken items by a specific date(according to the method date
     * parameter) for all the habits existing in the system. Selection is filtered
     * by habit assign suspended status which must be false. Result is sorted in
     * descending order, that is the most popular habit will be the first in a list
     * and the least popular will be the last.
     *
     * @param statisticCreationDate Statistic creation date.
     * @param languageCode          Language code of habit items, for example, 'en'.
     * @return {@link List} of {@link Tuple}s that contain item names and not taken
     *         amount of that items.
     * @author Shevtsiv Rostyslav
     */
    @Query(value = "SELECT ht.habitItem, SUM(hs.amountOfItems) "
        + "FROM HabitStatistic hs "
        + "     INNER JOIN HabitTranslation ht ON ht.habit.id = hs.habitAssign.habit.id "
        + "     WHERE upper(hs.habitAssign.status) <> 'SUSPENDED' "
        + "     AND cast(hs.createDate as date) = cast(:statisticCreationDate as date)"
        + "     AND ht.language.code = :languageCode "
        + "GROUP BY ht.habitItem "
        + "ORDER BY COUNT(hs.habitAssign.habit) DESC")
    List<Tuple> getStatisticsForAllHabitItemsByDate(
        @Param("statisticCreationDate") ZonedDateTime statisticCreationDate,
        @Param("languageCode") String languageCode);

    /**
     * Method for getting amount of {@link Habit} in progress by {@link User} id
     * (not suspended).
     *
     * @param id {@link User} id.
     * @return amount of habits in progress by {@link User} id.
     * @author Marian Datsko
     */
    @Query(value = "SELECT COUNT(ha.id) FROM HabitAssign ha "
        + " WHERE ha.user.id = :userId"
        + " AND upper(ha.status) = 'ACQURIED' ")
    Long getAmountOfHabitsInProgressByUserId(@Param("userId") Long id);

    /**
     * Method for getting amount of acquired {@link Habit} by {@link User} id.
     *
     * @param id {@link User} id.
     * @return amount of acquired habits by {@link User} id.
     * @author Marian Datsko
     */
    @Query(value = "SELECT COUNT(ha.id) FROM HabitAssign ha "
        + " WHERE ha.user.id = :userId"
        + " AND upper(ha.status) = 'SUSPENDED'")
    Long getAmountOfAcquiredHabitsByUserId(@Param("userId") Long id);
}