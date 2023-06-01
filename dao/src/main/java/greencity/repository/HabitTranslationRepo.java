package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Provides an interface to manage {@link HabitTranslation} entity.
 *
 * @author Volodymyr Turko
 */
public interface HabitTranslationRepo extends JpaRepository<HabitTranslation, Long> {
    /**
     * Method with return {@link Optional} of {@link HabitTranslation}.
     *
     * @param name     of {@link HabitTranslation}.
     * @param language code language.
     * @return {@link Optional} of {@link HabitTranslation}.
     */
    Optional<HabitTranslation> findByNameAndLanguageCode(String name, String language);

    /**
     * Method return {@link Optional} of {@link HabitTranslation}.
     *
     * @param habit    {@link Habit}.
     * @param language code language.
     * @return {@link Optional} of {@link HabitTranslation}.
     */
    Optional<HabitTranslation> findByHabitAndLanguageCode(Habit habit, String language);

    /**
     * Method returns available {@link HabitTranslation}'s for specific user.
     *
     * @param userId   {@link User} id which we use to filter.
     * @param language code language.
     * @return List of available {@link HabitTranslation}`s.
     */
    @Query(value = "SELECT ht FROM HabitTranslation ht "
        + "WHERE ht.language.code = :language AND ht.habit.id IN "
        + "(SELECT ha.habit.id FROM HabitAssign ha "
        + "WHERE ha.user.id = :userId AND upper(ha.status) <> 'AQCUIRED')")
    List<HabitTranslation> findHabitTranslationsByUserAndAcquiredStatus(@Param("userId") Long userId,
        @Param("language") String language);

    /**
     * Method returns all {@link Habit}'s by language.
     *
     * @param language code language.
     * @return Pageable of available {@link HabitTranslation}`s.
     * @author Dovganyuk Taras
     */
    Page<HabitTranslation> findAllByLanguageCode(Pageable pageable, String language);

    /**
     * Method deletes all {@link HabitTranslation}'s by {@link Habit} instance.
     *
     * @param habit {@link Habit} instance.
     */
    void deleteAllByHabit(Habit habit);

    /**
     * Method that find all habit's translations by language code and tags.
     *
     * @param pageable     {@link Pageable}
     * @param tags         {@link List} of {@link String} tags
     * @param languageCode language code {@link String}
     *
     * @return {@link List} of {@link HabitTranslation}.
     * @author Markiyan Derevetskyi
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags)))")
    Page<HabitTranslation> findAllByTagsAndLanguageCode(Pageable pageable, List<String> tags, String languageCode);

    /**
     * Method that find all habit's translations by tags, complexities,
     * isCustomHabit and language code.
     *
     * @param pageable      {@link Pageable}
     * @param tags          {@link List} of {@link String} tags
     * @param isCustomHabit {@link Boolean} value.
     * @param complexities  {@link Integer} value.
     * @param languageCode  language code {@link String}
     * @return {@link List} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE h.isCustomHabit = :isCustomHabit AND h.complexity IN (:complexities) AND t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags)))")
    Page<HabitTranslation> findAllByDifferentParameters(Pageable pageable, List<String> tags,
        Optional<Boolean> isCustomHabit, Optional<List<Integer>> complexities, String languageCode);

    /**
     * Method that find all habit's translations by isCustomHabit and language code.
     *
     * @param pageable      {@link Pageable}
     * @param isCustomHabit {@link Boolean} value.
     * @param languageCode  language code {@link String}
     * @return {@link List} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE h.isCustomHabit = :isCustomHabit)")
    Page<HabitTranslation> findAllByIsCustomHabitAndLanguageCode(Pageable pageable, Optional<Boolean> isCustomHabit,
        String languageCode);

    /**
     * Method that find all habit's translations by complexities and language code.
     *
     * @param pageable     {@link Pageable}
     * @param complexities {@link Integer} value.
     * @param languageCode language code {@link String}
     * @return {@link List} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE h.complexity IN (:complexities))")
    Page<HabitTranslation> findAllByComplexityAndLanguageCode(Pageable pageable, Optional<List<Integer>> complexities,
        String languageCode);

    /**
     * Method that find all habit's translations by tags, isCustomHabit and language
     * code.
     *
     * @param pageable      {@link Pageable}
     * @param tags          {@link List} of {@link String} tags
     * @param isCustomHabit {@link Boolean} value.
     * @param languageCode  language code {@link String}
     * @return {@link List} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE h.isCustomHabit = :isCustomHabit AND t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags)))")
    Page<HabitTranslation> findAllByTagsAndIsCustomHabitAndLanguageCode(Pageable pageable, List<String> tags,
        Optional<Boolean> isCustomHabit, String languageCode);

    /**
     * Method that find all habit's translations by tags, complexities and language
     * code.
     *
     * @param pageable     {@link Pageable}
     * @param tags         {@link List} of {@link String} tags
     * @param complexities {@link Integer} value.
     * @param languageCode language code {@link String}
     * @return {@link List} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE h.complexity IN (:complexities) AND t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags)))")
    Page<HabitTranslation> findAllByTagsAndComplexityAndLanguageCode(Pageable pageable, List<String> tags,
        Optional<List<Integer>> complexities, String languageCode);

    /**
     * Method that find all habit's translations by isCustomHabit, complexities and
     * language code.
     *
     * @param pageable      {@link Pageable}
     * @param isCustomHabit {@link Boolean} value.
     * @param complexities  {@link Integer} value.
     * @param languageCode  language code {@link String}
     * @return {@link List} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE h.isCustomHabit = :isCustomHabit AND h.complexity IN (:complexities))")
    Page<HabitTranslation> findAllByIsCustomHabitAndComplexityAndLanguageCode(Pageable pageable,
        Optional<Boolean> isCustomHabit, Optional<List<Integer>> complexities, String languageCode);

    /**
     * Method return {@link List} of {@link HabitTranslation} by habit.
     *
     * @param habit {@link Habit}.
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */
    List<HabitTranslation> findAllByHabit(Habit habit);
}
