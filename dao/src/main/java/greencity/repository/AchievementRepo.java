package greencity.repository;

import greencity.entity.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepo extends JpaRepository<Achievement, Long> {
    @Query(value = "Select ach.* from achievements as ach\n" +
            "            join user_achievements uach on ach.id = uach.achievement_id\n" +
            "            join user_actions ua on ua.count < ach.condition\n" +
            "            where uach.user_id=:userId and ach.achievement_category_id=:achievementCategoryId", nativeQuery = true)
    List<Achievement> findUnAchieved(Long userId, Long achievementCategoryId);


    /**
     * Searches for achievements based on a query string and returns a paginated
     * result.
     *
     * @param paging A Pageable object containing the pagination information (e.g.,
     *               page number, size, sort order).
     * @param query  The search query string to filter achievements.
     * @return A Page of Achievement objects that match the search query.
     */
    @Query("SELECT DISTINCT a FROM Achievement a "
        + "WHERE CONCAT(a.id,'') LIKE LOWER(CONCAT('%', :query, '%')) "
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

    /**
     * Searches for achievements that have not yet been achieved by the specified
     * user.
     *
     * @param userId The ID of the user for whom to find unachieved achievements.
     * @return A list of achievements that the user has not yet achieved.
     */
    @Query(value = "Select a.*" +
            "from user_achievements as uach" +
            "         join achievements a on uach.achievement_id = a.id" +
            "         join user_actions ua on a.achievement_category_id = ua.achievement_category_id" +
            "where uach.user_id =:userId" +
            "  and ua.count < a.condition;", nativeQuery = true)
    List<Achievement> searchAchievementsUnAchieved(Long userId);
}
