package greencity.repository;

import greencity.entity.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementRepo extends JpaRepository<Achievement, Long> {
    /**
     * Method returns {@link Achievement} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link Achievement}.
     * @author Orest Mamchuk
     */
    @Query("SELECT DISTINCT a FROM Achievement a "
        + "JOIN AchievementTranslation at on at.achievement.id = a.id "
        + "WHERE CONCAT(a.id,'') LIKE LOWER(CONCAT('%', :query, '%')) "
        + "OR LOWER(at.title) LIKE LOWER(CONCAT('%', :query, '%'))"
        + "OR LOWER(at.description) LIKE LOWER(CONCAT('%', :query, '%'))"
        + "OR LOWER(at.message) LIKE LOWER(CONCAT('%', :query, '%'))"
        + "OR LOWER(a.achievementCategory.name) LIKE LOWER(CONCAT('%', :query, '%'))"
        + "OR CONCAT(a.condition, ' ') LIKE LOWER(CONCAT('%', :query, '%'))")
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
