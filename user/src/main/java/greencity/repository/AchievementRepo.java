package greencity.repository;

import greencity.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepo extends JpaRepository<Achievement, Long> {
}
