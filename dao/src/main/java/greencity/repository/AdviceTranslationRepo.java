package greencity.repository;

import greencity.entity.Advice;
import greencity.entity.Language;
import greencity.entity.localization.AdviceTranslation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AdviceTranslationRepo extends JpaRepository<AdviceTranslation, Long> {
    /**
     * Method that returns list of {@link AdviceTranslation} and also fetches
     * {@link Advice} and {@link Language}.
     *
     * @return list of {@link AdviceTranslation}
     * @author Markiyan Derevetskyi
     */
    @Query(value = "select at from AdviceTranslation at join fetch at.advice join fetch at.language order by at.id")
    List<AdviceTranslation> findAll();

    /**
     * Method for getting random content by habit id and language code. This method
     * use native SQL query to reduce the load on the backend
     *
     * @param habitId Id of HabitDictionary
     * @return {@link AdviceTranslation} in Optional
     * @author Vitaliy Dzen
     */
    @Query(nativeQuery = true, value = "SELECT * FROM advice_translations WHERE language_id = "
        + "(SELECT id FROM languages WHERE code = ?1)"
        + " AND advice_id = "
        + "(SELECT id FROM advices WHERE habit_id = ?2 ORDER BY RANDOM() LIMIT 1);")
    Optional<AdviceTranslation> getRandomAdviceTranslationByHabitIdAndLanguage(String languageCode, Long habitId);

    /**
     * Method find list of {@link AdviceTranslation} of Advices by habitId and
     * language. use native SQL query to reduce the load on the backend
     *
     * @param habitId Id of Habit
     * @return {@link AdviceTranslation} in Optional
     * @author Vira Maksymets
     */
    @Query(nativeQuery = true, value = "SELECT * FROM advice_translations WHERE language_id = "
        + "(SELECT id FROM languages WHERE code = ?2)"
        + " AND advice_id IN "
        + "(SELECT id FROM advices WHERE habit_id = ?1);")
    List<AdviceTranslation> getAllByHabitIdAndLanguageCode(Long habitId, String languageCode);

    /**
     * Method find {@link AdviceTranslation} by content and language code.
     *
     * @param languageCode of {@link Language}
     * @param content      of {@link Advice}
     * @return {@link AdviceTranslation} in Optional
     * @author Vitaliy Dzen
     */
    Optional<AdviceTranslation> findAdviceTranslationByLanguageCodeAndContent(String languageCode, String content);

    /**
     * Method deletes all {@link AdviceTranslation}'s by {@link Advice} instance.
     *
     * @param advice {@link Advice} instance.
     */
    @Transactional
    void deleteAllByAdvice(Advice advice);
}
