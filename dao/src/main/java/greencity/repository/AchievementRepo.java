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
    /**
     * Retrieves a list of achievements that a specific user hasn't achieved yet
     * within a specified achievement category. The method identifies unachieved
     * achievements by comparing user actions count with the conditions of
     * achievements and by checking if a user already has the achievement in the
     * user_achievements table.
     *
     * @param userId                The unique identifier of the user.
     * @param achievementCategoryId The unique identifier of the achievement
     *                              category.
     * @return A list of Achievement objects that the user hasn't achieved within
     *         the specified category.
     */
    @Query(value = "SELECT ach.* "
        + "FROM achievements ach "
        + "WHERE ach.id IN ("
        + "    SELECT achievement_id "
        + "    FROM user_achievements uach "
        + "    WHERE uach.user_id = :userId"
        + "    AND uach.habit_id = :habitId"
        + ") "
        + "AND ach.condition > ("
        + "    SELECT ua.count "
        + "    FROM user_actions ua "
        + "    WHERE ua.user_id = :userId "
        + "    AND ua.achievement_category_id = :achievementCategoryId"
        + ") "
        + "AND ach.achievement_category_id = :achievementCategoryId ",
        nativeQuery = true)
    List<Achievement> findUnAchieved(Long userId, Long achievementCategoryId, Long habitId);

    /**
     * Retrieves a list of achievements that a specific user hasn't achieved yet
     * within a specified achievement category. The method identifies unachieved
     * achievements by comparing user actions count with the conditions of
     * achievements and by checking if a user already has the achievement in the
     * user_achievements table.
     *
     * @param userId                The unique identifier of the user.
     * @param achievementCategoryId The unique identifier of the achievement
     *                              category.
     * @return A list of Achievement objects that the user hasn't achieved within
     *         the specified category.
     */
    @Query(value = """
        Select ach.*
        from achievements as ach
        where
            ach.id in
              (SELECT achievement_id from user_achievements uach where uach.user_id = :userId)
          and
            ach.condition > (SELECT ua.count
                                              from user_actions ua
                                              where ua.user_id = :userId and
                                                    ua.achievement_category_id=:achievementCategoryId)
        and
            ach.achievement_category_id=:achievementCategoryId""",
        nativeQuery = true)
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
    @Query(value = "SELECT * from achievements "
        + "where id not in (select achievement_id "
        + "                 from user_achievements "
        + "                 where user_id = :userId)", nativeQuery = true)
    List<Achievement> searchAchievementsUnAchieved(Long userId);
}
