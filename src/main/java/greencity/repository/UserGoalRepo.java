package greencity.repository;

import greencity.entity.Goal;
import greencity.entity.User;
import greencity.entity.UserGoal;
import java.util.List;
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
}
