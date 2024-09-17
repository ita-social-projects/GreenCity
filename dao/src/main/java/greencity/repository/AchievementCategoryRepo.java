package greencity.repository;

import greencity.entity.AchievementCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AchievementCategoryRepo extends JpaRepository<AchievementCategory, Long> {
    /**
     * Finds {@link AchievementCategory} by name.
     *
     * @param name to find by.
     * @return a category by name.
     */
    Optional<AchievementCategory> findByName(String name);
}
