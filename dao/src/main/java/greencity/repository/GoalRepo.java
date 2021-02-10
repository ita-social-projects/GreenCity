package greencity.repository;

import greencity.entity.Goal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepo extends JpaRepository<Goal, Long>, JpaSpecificationExecutor<Goal> {
    /**
     * Method returns {@link Goal} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link Goal}.
     */
    @Query("SELECT g FROM Goal g join g.translations as gt"
        + " WHERE CONCAT(g.id,'') LIKE LOWER(CONCAT('%', :query, '%')) "
        + "OR LOWER(gt.language.code) LIKE LOWER(CONCAT('%', :query, '%'))"
        + "OR LOWER(gt.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Goal> searchBy(Pageable paging, String query);

    /**
     * Method returns list Goal id which are not in the habit.
     *
     * @param habitId habit id
     * @return list of id.
     */
    @Query(nativeQuery = true,
        value = "select goals.id from goals  where id not in"
            + " (select goal_id from habit_goals where habit_id = :habitId and "
            + "habit_goals.status like 'ACTUAL');")
    List<Long> getAllGoalByHabitIdNotContained(@Param("habitId") Long habitId);

    /**
     * Method returns list Goal id which are in the habit.
     *
     * @param habitId habit id
     * @return list of id.
     */
    @Query(nativeQuery = true,
        value = "select goal_id from habit_goals  where habit_id = :habitId and habit_goals.status like 'ACTUAL';")
    List<Long> getAllGoalByHabitIdISContained(@Param("habitId") Long habitId);

    /**
     * Method returns {@link Goal} by list Goal id and pageable.
     *
     * @param listId habit id
     * @return list of {@link Goal}.
     */
    @Query("select g from Goal g where g.id in(:listId)")
    Page<Goal> getGoalByListOfIdPageable(List<Long> listId, Pageable pageable);

    /**
     * Method returns {@link Goal} by list Goal id.
     *
     * @param listId habit id
     * @return list of {@link Goal}.
     */
    @Query("select g from Goal g where g.id in(:listId)")
    List<Goal> getGoalByListOfId(List<Long> listId);
}
