package greencity.repository;

import greencity.entity.User;
import greencity.entity.localization.AchievementTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementTranslationRepo extends JpaRepository<AchievementTranslation, Long> {
    /**
     * Method finding achievements with status active and no notify.
     *
     * @param userId of {@link User}
     * @param langId of {@link Long}
     * @return list {@link AchievementTranslation}
     */
    @Query(nativeQuery = true, value = "SELECT * FROM achievement_translations t inner join user_achievements u "
        + "on t.achievement_id = u.achievement_id WHERE "
        + "u.user_id =:userId AND t.language_id =:langId AND u.achievement_status = 'ACTIVE' "
        + "AND u.notified = FALSE ;")
    List<AchievementTranslation> findAchievementsWithStatusActive(Long userId, Long langId);
}
