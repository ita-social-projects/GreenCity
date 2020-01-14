package greencity.repository;

import greencity.entity.HabitDictionary;
import greencity.entity.HabitDictionaryTranslation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Provides an interface to manage {@link HabitDictionaryTranslation} entity.
 *
 * @author Volodymyr Turko
 */
public interface HabitDictionaryTranslationRepo extends JpaRepository<HabitDictionaryTranslation, Long> {
    /**
     * Method with return {@link Optional} of {@link HabitDictionary} by comment id.
     *
     * @param name of {@link HabitDictionary}.
     * @return {@link Optional} of {@link HabitDictionary}.
     */
    Optional<HabitDictionaryTranslation> findByNameAndLanguage(String name, String language);

    /**
     * djdjdjjd.
     *
     * @param habitDictionary dddd.
     * @param language        ddddd.
     * @return null.
     */
    Optional<HabitDictionaryTranslation> findByHabitDictionaryAndLanguageCode(HabitDictionary habitDictionary,
                                                                              String language);

    /**
     * Method returns available habit dictionary for specific user.
     *
     * @param userId   user which we use to filter.
     * @param language code language.
     * @return List of available {@link HabitDictionaryTranslation}`s.
     */
    @Query(nativeQuery = true, value = "SELECT *  FROM habit_dictionary_translation WHERE"
        + " habit_dictionary_id NOT IN\n"
        + " (SELECT habits.habit_dictionary_id FROM habits WHERE user_id = ?1 AND status = 'true')\n"
        + "AND habit_dictionary_translation.language_id IN (SELECT id FROM languages WHERE code = ?2);")
    List<HabitDictionaryTranslation> findAvailableHabitDictionaryByUser(Long userId, String language);
}
