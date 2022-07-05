package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.entity.Language;
import greencity.enums.FactOfDayStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitFactTranslationRepo extends JpaRepository<HabitFactTranslation, Long> {
    /**
     * Method for getting random content by {@link Habit} id and {@link Language}
     * code. This method use native SQL query to reduce the load on the backend.
     *
     * @param habitId {@link Habit} Id.
     * @return {@link HabitFactTranslation} in Optional.
     * @author Vitaliy Dzen.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM habit_fact_translations WHERE language_id = "
        + "(SELECT id FROM languages WHERE code = :languageCode)"
        + " AND habit_fact_id = "
        + "(SELECT id FROM habit_facts WHERE habit_id = :habitId ORDER BY RANDOM() LIMIT 1);")
    Optional<HabitFactTranslation> getRandomHabitFactTranslationByHabitIdAndLanguage(String languageCode, Long habitId);

    /**
     * Method find {@link HabitFactTranslation} by content and language code.
     *
     * @param languageCode of {@link Language}.
     * @param content      of {@link HabitFact}.
     * @return {@link HabitFactTranslation} in Optional.
     * @author Vitaliy Dzen.
     */
    Optional<HabitFactTranslation> findFactTranslationByLanguageCodeAndContent(String languageCode,
        String content);

    /**
     * Method finds random {@link HabitFact} in 3 languages between all facts that
     * were not used during this iteration.
     *
     * @return optional list of {@link HabitFactTranslation}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM  habit_fact_translations "
        + "WHERE fact_of_day_status = 0 ORDER BY RANDOM() LIMIT 1;")
    List<HabitFactTranslation> findRandomHabitFact();

    /**
     * Method to get list of {@link HabitFactTranslation} specified by status and
     * {@link Language}.
     *
     * @param factOfDayStatus {@link FactOfDayStatus} shows if {@link HabitFact} was
     *                        or will be in future {@link HabitFact} of the day.
     * @param languageId      of {@link Language}.
     * @return list of {@link HabitFactTranslation} that satisfy the conditions.
     */
    @Query("SELECT ht FROM HabitFactTranslation ht"
        + " WHERE ht.factOfDayStatus = :factOfDayStatus AND ht.language.id = :languageId")
    HabitFactTranslation findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus factOfDayStatus,
        Long languageId);

    /**
     * Method to replace all outdated {@link FactOfDayStatus} by updated.
     *
     * @param outdated {@link FactOfDayStatus} that needs to be changed.
     * @param updated  new {@link FactOfDayStatus}.
     */
    @Modifying
    @Query("UPDATE HabitFactTranslation f SET f.factOfDayStatus = :updated WHERE f.factOfDayStatus = :outdated")
    void updateFactOfDayStatus(@Param("outdated") FactOfDayStatus outdated, @Param("updated") FactOfDayStatus updated);

    /**
     * Method to change {@link HabitFact} of day status for all facts with certain
     * {@link HabitFact} id.
     *
     * @param status      new {@link HabitFact} of day status.
     * @param habitfactId {@link HabitFact} id of group of facts which changes
     *                    {@link HabitFact} of day status.
     */
    @Modifying
    @Query("UPDATE HabitFactTranslation f SET f.factOfDayStatus = :status WHERE f.habitFact.id = :habitFactId")
    void updateFactOfDayStatusByHabitFactId(@Param("status") FactOfDayStatus status,
        @Param("habitFactId") Long habitfactId);

    /**
     * Method deletes all {@link HabitFactTranslation}'s by {@link Habit} instance.
     *
     * @param habitFact {@link HabitFact} instance.
     */
    @Transactional
    void deleteAllByHabitFact(HabitFact habitFact);

    /**
     * Method returns all {@link HabitFactTranslation} by languageCode and page.
     *
     * @param page         of habit facts.
     * @param languageCode of titleTranslation.
     * @return all {@link HabitFactTranslation} by languageCode and page.
     */
    Page<HabitFactTranslation> findAllByLanguageCode(Pageable page, String languageCode);
}
