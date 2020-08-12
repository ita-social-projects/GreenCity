package greencity.repository;

import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.entity.Goal;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * Method returns shopping list by user id.
     *
     * @return shopping list {@link ShoppingListDtoResponse}.
     * @author Marian Datsko
     */

    @Query(nativeQuery = true,
        value = "SELECT ug.status, gt.text FROM user_goals AS ug "
            + " JOIN goal_translations AS gt ON gt.goal_id = ug.goal_id "
            + " JOIN languages AS l ON l.id = gt.language_id "
            + " WHERE ug.goal_id IS NOT NULL AND "
            + " ug.user_id = :userId AND l.code = :languageCode "
            + " UNION "
            + " SELECT ug.status, cg.text "
            + " FROM user_goals AS ug "
            + " JOIN custom_goals AS cg ON cg.id = ug.custom_goal_id "
            + " WHERE ug.custom_goal_id IS NOT NULL AND ug.user_id = :userId ")
    List<Object> getShoppingList(@Param(value = "userId") Long userId,
                                                  @Param(value = "languageCode") String languageCode);
}
