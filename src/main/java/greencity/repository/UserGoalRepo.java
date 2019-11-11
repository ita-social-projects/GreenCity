package greencity.repository;

import greencity.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGoalRepo extends JpaRepository<UserGoal, Long> {
}
