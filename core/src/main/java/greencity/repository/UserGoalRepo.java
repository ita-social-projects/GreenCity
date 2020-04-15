package greencity.repository;

import greencity.entity.UserGoal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGoalRepo extends JpaRepository<UserGoal, Long> {
    /**
     * Method returns list of {@link UserGoal} for specific user.
     *
     * @param userId - id of user.
     * @return list of {@link UserGoal}
     */
    List<UserGoal> findAllByUserId(Long userId);
}
