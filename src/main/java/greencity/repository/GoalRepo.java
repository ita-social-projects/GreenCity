package greencity.repository;

import greencity.entity.Goal;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
