package greencity.repository;

import greencity.entity.CustomGoal;
import greencity.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link CustomGoal} entity.
 */
@Repository
public interface CustomGoalRepo extends JpaRepository<CustomGoal, Long> {
    /**
     * Method returns list of available (not ACTIVE) custom goal for user.
     *
     * @param userId id of the {@link User} current user
     * @return list of {@link CustomGoal}
     */
    @Query("SELECT cg FROM CustomGoal cg WHERE cg.id NOT IN"
        + "(SELECT ug.customGoal FROM UserGoal ug WHERE ug.user.id=:userId "
        + "AND ug.status='ACTIVE') AND cg.user.id=:userId")
    List<CustomGoal> findAllAvailableCustomGoalsForUserId(@Param("userId") Long userId);

    /**
     * Method returns particular selected custom goal for user.
     *
     * @param userId id of the {@link User} current user
     * @return {@link CustomGoal}
     */
    @Query("SELECT cg FROM CustomGoal cg WHERE cg.id IN"
        + "(SELECT ug.customGoal FROM UserGoal ug WHERE ug.user.id=:userId AND ug.id=:userGoalId) "
        + "AND cg.user.id=:userId")
    CustomGoal findByUserGoalIdAndUserId(@Param("userGoalId") Long userGoalId, @Param("userId") Long userId);

    /**
     * Method find all custom goals by user.
     *
     * @param id {@link CustomGoal} id
     * @return list of {@link CustomGoal}
     */
    List<CustomGoal> findAllByUserId(Long id);

    /**
     * Method change custom goal status.
     *
     * @author Marian Datsko
     */
    @Modifying
    @Query(nativeQuery = true, value = " UPDATE user_goals "
        + " SET status = :status, date_completed = :date WHERE custom_goal_id = :id AND user_id = :userId ")
    void changeCustomGoalStatus(@Param(value = "userId") Long userId,
        @Param(value = "id") Long id,
        @Param(value = "status") String status,
        @Param(value = "date") LocalDateTime date);
}
