package greencity.repository;

import greencity.entity.HabitStatistic;
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
     * dsadspa.
     *
     * @param id dasda.
     * @return
     */
    @Query(value = "SELECT SUM(hs.amountOfItems) FROM HabitStatistic hs WHERE habit_id=:id"
        + " AND MONTH(createdOn) = MONTH(CURRENT_DATE())\n"
        + " AND YEAR(createdOn) = YEAR(CURRENT_DATE())")
    Optional<Integer> getSumOfAllItems(@Param("id") Long id);

    /**
     * Find all statistics by habit id.
     *
     * @param id .
     * @return addsadsa
     */
    List<HabitStatistic> findAllByHabitId(Long id);

    /**
     * adsdas.
     *
     * @param habitId dsa.
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT amount_of_items - (SELECT amount_of_items FROM habit_statistics\n"
        + " WHERE habit_statistics.date = CURRENT_DATE - INTERVAL 1 MONTH AND habit_id=?1)  "
        + " FROM habit_statistics\n"
        + " WHERE habit_statistics.date = CURRENT_DATE AND habit_id=?1")
    Optional<Integer> getDifferenceItemsWithPreviousMonth(@Param("habitId") Long habitId);
}