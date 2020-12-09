package greencity.repository;

import greencity.entity.localization.GoalTranslation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalTranslationRepo extends JpaRepository<GoalTranslation, Long> {
    /**
     * Method for getting all goal translations for given language.
     *
     * @param languageCode code of needed language
     * @return List of {@link GoalTranslation}, that contains all goal translations
     *         for needed language.
     */
    List<GoalTranslation> findAllByLanguageCode(String languageCode);

    /**
     * Method returns available goal translations for specific user and language
     * code.
     *
     * @param userId       target user id
     * @param languageCode code of needed language
     * @return List of available {@link GoalTranslation}'s.
     */
    @Query("SELECT g FROM GoalTranslation g WHERE g.goal.id NOT IN "
        + "(SELECT ug.goal FROM UserGoal ug WHERE ug.habitAssign.id = ?1 AND ug.status = 'ACTIVE') "
        + "AND g.language.code = ?2")
    List<GoalTranslation> findAvailableByUserId(Long userId, String languageCode);

    /**
     * Method returns goal translation for particular selected goal for specific
     * user and language code.
     *
     * @param goalId       target user id
     * @param languageCode code of needed language
     * @return {@link GoalTranslation}
     */
    @Query(nativeQuery = true, value = "SELECT * FROM goal_translations as g "
        + "where g.goal_id = (SELECT ug.goal_id FROM user_goals as ug WHERE ug.id=:goalId)"
        + "AND g.language_id = (SELECT id FROM languages l where l.code =:languageCode)")
    GoalTranslation findByLangAndUserGoalId(String languageCode, Long goalId);

    /**
     * Method updates goal translation for particular selected goal for specific
     * language code.
     *
     * @param goalId       target goal id
     * @param languageCode code of needed language
     * @param content      new content
     * @return {@link GoalTranslation}
     */
    @Query("UPDATE GoalTranslation SET content=?3"
        + "WHERE id = ?1 AND language.code = ?2")
    GoalTranslation updateTranslationContent(Long goalId, String languageCode, String content);

    /**
     * Method for getting all goal translations for given habit in specific
     * language.
     *
     * @param languageCode code of needed language
     * @param habitId      code of needed language
     * @return List of {@link GoalTranslation}, that contains all goal translations
     *         for needed habit.
     */
    @Query("SELECT gt FROM GoalTranslation gt JOIN Goal g ON g.id = gt.goal.id "
        + "JOIN g.habits h ON h.id = :habitId"
        + " WHERE gt.language.code = :languageCode")
    List<GoalTranslation> findAllGoalByHabitIdAndByLanguageCode(String languageCode,
        @Param(value = "habitId") Long habitId);
}
