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
     * Method return {@link Optional} of {@link HabitDictionaryTranslation}.
     *
     * @param habitDictionary {@link HabitDictionary}.
     * @param language code language.
     * @return {@link HabitDictionaryTranslation}.
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
    @Query(nativeQuery = true, value = "SELECT * FROM habit_dictionary_translation "
        + "INNER JOIN languages l on l.id = habit_dictionary_translation.language_id "
        + "WHERE l.code = ?2 AND habit_dictionary_id NOT IN "
        + "(SELECT habits.habit_dictionary_id FROM habits "
        + "INNER JOIN habits_users_assign hua ON hua.habit_id = id "
        + "WHERE hua.users_id = ?1 AND status = 'true');")
    List<HabitDictionaryTranslation> findAvailableHabitDictionaryByUser(Long userId, String language);
}