package greencity.repository;

import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.entity.Goal;
import greencity.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepo extends JpaRepository<Goal, Long> {
    /**
     * Method returns available goals for specific user.
     *
     * @return List of available {@link Goal}'s.
     */

    @Query("SELECT g from Goal g WHERE g.id NOT IN "
        + "(SELECT ug.goal FROM UserGoal ug WHERE ug.user = ?1 AND ug.status = 'ACTIVE')")
    List<Goal> findAvailableGoalsByUser(User user);

    /**
     * Method change goal status.
     *
     * @author Marian Datsko
     */
    @Modifying
    @Query(nativeQuery = true, value = " UPDATE user_goals "
        + " SET status = :status, date_completed = :date WHERE goal_id = :id AND user_id = :userId ")
    void changeGoalStatus(@Param(value = "userId") Long userId,
                          @Param(value = "id") Long id,
                          @Param(value = "status") String status,
                          @Param(value = "date") LocalDateTime date);
}
