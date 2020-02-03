package greencity.repository;

import greencity.entity.CustomGoal;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * @param user {@link User} current user
     * @return list of {@link CustomGoal}
     */
    @Query("SELECT cg FROM CustomGoal cg WHERE cg.id NOT IN"
        + "(SELECT ug.customGoal FROM UserGoal ug WHERE ug.user=:user "
        + "AND ug.status='ACTIVE') AND cg.user=:user")
    List<CustomGoal> findAllAvailableCustomGoalsForUser(@Param("user") User user);

    /**
     * Method find all custom goals by user.
     *
     * @param id {@link CustomGoal} id
     * @return list of {@link CustomGoal}
     */
    List<CustomGoal> findAllByUserId(Long id);
}
