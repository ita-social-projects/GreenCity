package greencity.repository;

import greencity.entity.AchievementCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementCategoryRepo extends JpaRepository<AchievementCategory, Long> {
    /**
     * Finds {@link AchievementCategory} by name.
     *
     * @param name to find by.
     * @return a category by name.
     */
    AchievementCategory findByName(String name);
}
