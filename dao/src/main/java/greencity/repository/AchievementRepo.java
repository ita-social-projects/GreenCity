package greencity.repository;

import greencity.entity.Achievement;
import greencity.enums.UserActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * Finds all achievements that have a given {@link UserActionType} in condition.
     *
     * @param actionType {@link UserActionType}
     * @return {@link List} of {@link Achievement}.
     */
    @Query(value = "SELECT * FROM achievements a "
        + "WHERE (a.condition->:#{#actionType.name()}) IS NOT NULL ", nativeQuery = true)
    List<Achievement> findAllByActionType(UserActionType actionType);
}
