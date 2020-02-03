package greencity.repository;

import greencity.entity.Goal;
import greencity.entity.User;
import greencity.entity.localization.GoalTranslation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoalTranslationRepo extends JpaRepository<GoalTranslation, Long> {
    /**
     * Method for getting all goal translations for given language.
     *
     * @param languageCode code of needed language
     * @return List of {@link GoalTranslation}, that contains all goal translations for needed language.
     */
    List<GoalTranslation> findAllByLanguageCode(String languageCode);

    /**
     * Method returns available goal translations for specific user and language code.
     *
     * @param userId         target user id
     * @param languageCode code of needed language
     * @return List of available {@link GoalTranslation}'s.
     */
    @Query("SELECT g FROM GoalTranslation g WHERE g.goal.id NOT IN "
        + "(SELECT ug.goal FROM UserGoal ug WHERE ug.user.id = ?1 AND ug.status = 'ACTIVE') "
        + "AND g.language.code = ?2")
    List<GoalTranslation> findAvailableByUserId(Long userId, String languageCode);

    /**
     * Method returns goal translations for specific goal and language code.
     *
     * @param goal         goal to get translation
     * @param languageCode code of needed language
     * @return Optional of {@link GoalTranslation}
     */
    Optional<GoalTranslation> findByGoalAndLanguageCode(Goal goal, String languageCode);
}
