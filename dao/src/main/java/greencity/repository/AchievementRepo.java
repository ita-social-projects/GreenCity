package greencity.repository;

import greencity.entity.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementRepo extends JpaRepository<Achievement, Long> {
    /**
     * Searches for achievements based on a query string and returns a paginated
     * result.
     *
     * @param paging A Pageable object containing the pagination information (e.g.,
     *               page number, size, sort order).
     * @param query  The search query string to filter achievements.
     * @return A Page of Achievement objects that match the search query.
     */
    Page<Achievement> searchAchievementsBy(Pageable paging, String query);

    /**
     * Method find {@link Achievement} by categoryId and condition.
     *
     * @param achievementCategoryId of {@link Achievement}
     * @param condition             of {@link Achievement}
     * @return Achievement
     * @author Orest Mamchuk
     */
    Optional<Achievement> findByAchievementCategoryIdAndCondition(Long achievementCategoryId, Integer condition);
}
