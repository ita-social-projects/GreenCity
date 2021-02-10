package greencity.repository;

import greencity.entity.Habit;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Habit} entity.
 */
@Repository
public interface HabitRepo extends JpaRepository<Habit, Long> {
    /**
     * Method add goal to habit by id and status ACTIVE. This method use native SQL
     * query.
     *
     * @param habitID Id of Habit
     * @param goalID  Id of Goal
     * @author Marian Diakiv
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into habit_goals(habit_id,goal_id)"
        + "values (:habitID,:goalID);")
    void addShopingListItemToHabit(@Param("habitID") Long habitID, @Param("goalID") Long goalID);

    /**
     * Method to change status. This method use native SQL query.
     *
     * @param habitID Id of Habit
     * @param goalID  Id of Goal
     * @author Marian Diakiv
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update habit_goals set status = 'DELETED'"
        + " where habit_goals.habit_id = :habitID and habit_goals.goal_id = :goalID"
        + " and habit_goals.status like 'ACTUAL'")
    void upadateShopingListItemInHabit(@Param("habitID") Long habitID, @Param("goalID") Long goalID);
}
