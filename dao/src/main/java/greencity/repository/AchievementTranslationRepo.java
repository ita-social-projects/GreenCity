package greencity.repository;

import greencity.entity.localization.AchievementTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import greencity.entity.User;

import java.util.List;

public interface AchievementTranslationRepo extends JpaRepository<AchievementTranslation, Long> {
    /**
     * Finds translations for all unnotified achievements of user.
     *
     * @param userId of {@link User}
     * @param langId of {@link Long}
     * @return list {@link AchievementTranslation}
     */
    @Query(nativeQuery = true, value = "SELECT * FROM achievement_translations t inner join user_achievements u "
        + "on t.achievement_id = u.achievement_id WHERE "
        + "u.user_id =:userId AND t.language_id =:langId "
        + "AND u.notified = FALSE ;")
    List<AchievementTranslation> findAllUnnotifiedForUser(Long userId, Long langId);
}
