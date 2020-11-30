package greencity.repository;

import greencity.entity.Goal;
import greencity.entity.UserGoal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserGoalRepo extends JpaRepository<UserGoal, Long> {
    /**
     * Method returns list of {@link UserGoal} for specific user.
     *
     * @param userId - id of user.
     * @return list of {@link UserGoal}
     */
    List<UserGoal> findAllByUserId(Long userId);

    /**
     * Method returns predefined goal as specific UserGoal.
     *
     * @param userGoalId - id of userGoal.
     * @return {@link Goal}
     */
    @Query("SELECT ug.goal FROM UserGoal ug WHERE ug.id = ?1")
    Optional<Goal> findGoalByUserGoalId(Long userGoalId);
}
