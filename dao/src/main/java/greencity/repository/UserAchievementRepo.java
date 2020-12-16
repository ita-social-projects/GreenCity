package greencity.repository;

import greencity.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAchievementRepo extends JpaRepository<UserAchievement, Long> {
}
