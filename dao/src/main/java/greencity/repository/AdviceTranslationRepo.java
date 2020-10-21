package greencity.repository;

import greencity.entity.Advice;
import greencity.entity.Language;
import greencity.entity.localization.AdviceTranslation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdviceTranslationRepo extends JpaRepository<AdviceTranslation, Long> {
    /**
     * Method for getting random content by habit id and language code.
     * This method use native SQL query to reduce the load on the backend
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
     * Method find {@link AdviceTranslation} by content and language code.
     *
     * @param languageCode of {@link Language}
     * @param content       of {@link Advice}
     * @return {@link AdviceTranslation} in Optional
     * @author Vitaliy Dzen
     */
    Optional<AdviceTranslation> findAdviceTranslationByLanguageCodeAndContent(String languageCode, String content);

    /**
     * Method deletes all {@link AdviceTranslation}'s by {@link Advice} instance.
     *
     * @param advice {@link Advice} instance.
     */
    void deleteAllByAdvice(Advice advice);
}
