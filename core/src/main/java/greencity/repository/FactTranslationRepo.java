package greencity.repository;

import greencity.entity.FactTranslation;
import greencity.entity.HabitFact;
import greencity.entity.Language;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FactTranslationRepo extends JpaRepository<FactTranslation, Long> {
    /**
     * Method for getting random content by habit id and language code.
     * This method use native SQL query to reduce the load on the backend
     *
     * @param habitId Id of HabitDictionary
     * @return {@link FactTranslation} in Optional
     * @author Vitaliy Dzen
     */
    @Query(nativeQuery = true, value = "SELECT * FROM fact_translations WHERE language_id = "
        + "(SELECT id FROM languages WHERE code = ?1)"
        + " AND habit_fact_id = "
        + "(SELECT id FROM habit_facts WHERE habit_dictionary_id = ?2 ORDER BY RANDOM() LIMIT 1);")
    Optional<FactTranslation> getRandomFactTranslationByHabitIdAndLanguage(String languageCode, Long habitId);

    /**
     * Method find {@link FactTranslation} by content and language code.
     *
     * @param languageCode of {@link Language}
     * @param habitFact    of {@link HabitFact}
     * @return {@link FactTranslation} in Optional
     * @author Vitaliy Dzen
     */
    Optional<FactTranslation> findFactTranslationByLanguage_CodeAndHabitFact(String languageCode, String habitFact);
}
