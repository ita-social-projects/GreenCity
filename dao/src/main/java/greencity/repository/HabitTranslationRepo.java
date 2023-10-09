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
     * Method returns all default and custom which created by current user his
     * friends {@link Habit}'s by language.
     *
     * @param pageable          {@link Pageable}.
     * @param language          code language.
     * @param availableUsersIds {@link Long}
     *
     * @return Pageable of available {@link HabitTranslation}`s.
     * @author Dovganyuk Taras
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :language) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE ( h.isCustomHabit = true AND h.userId IN (:availableUsersIds)) or h.isCustomHabit = false ) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByLanguageCode(Pageable pageable, String language, List<Long> availableUsersIds);

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
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByTagsAndLanguageCode(Pageable pageable, List<String> tags, String languageCode);

    /**
     * Method that find all habit's translations by language code and tags.
     *
     * @param pageable     {@link Pageable}
     * @param tags         {@link List} of {@link String} tags
     * @param languageCode language code {@link String}
     *
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE((h.isCustomHabit = true AND h.userId IN (:availableUsersIds)) OR (h.isCustomHabit = false ))"
        + " AND t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByTagsAndLanguageCodeAndForAvailableUsersIfIsCustomHabitTrue(Pageable pageable,
        List<String> tags, String languageCode,
        List<Long> availableUsersIds);

    /**
     * Method that find all habit's translations by tags, complexities, language
     * code in case when isCustomHabit false.
     *
     * @param pageable     {@link Pageable}.
     * @param tags         {@link List} of {@link String}.
     * @param complexities {@link List} of {@link Integer}.
     * @param languageCode language code {@link String}.
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
        + "WHERE h.isCustomHabit = false AND h.complexity IN (:complexities) AND t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByDifferentParametersIsCustomHabitFalse(Pageable pageable, List<String> tags,
        Optional<List<Integer>> complexities, String languageCode);

    /**
     * Method that find all habit's translations by tags, complexities, language
     * code in case when isCustomHabit true.
     *
     * @param pageable          {@link Pageable}.
     * @param tags              {@link List} of {@link String}.
     * @param complexities      {@link List} of {@link Integer}.
     * @param languageCode      language code {@link String}.
     * @param availableUsersIds {@link Long}
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE h.isCustomHabit = true AND h.userId IN (:availableUsersIds) "
        + "AND h.complexity IN (:complexities) AND t.id IN"
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByDifferentParametersIsCustomHabitTrue(Pageable pageable, List<String> tags,
        Optional<List<Integer>> complexities, String languageCode, List<Long> availableUsersIds);

    /**
     * Method that find all habit's translations by language code in case when
     * isCustomHabit true.
     *
     * @param pageable     {@link Pageable}
     * @param languageCode language code {@link String}
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE h.isCustomHabit = true AND h.userId IN (:availableUsersIds)) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByIsCustomHabitTrueAndLanguageCode(Pageable pageable,
        String languageCode, List<Long> availableUsersIds);

    /**
     * Method that find all habit's translations by language code in case when
     * isCustomHabit false.
     *
     * @param pageable     {@link Pageable}
     * @param languageCode language code {@link String}
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE h.isCustomHabit = false) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByIsCustomFalseHabitAndLanguageCode(Pageable pageable, String languageCode);

    /**
     * Method that find all habit's translations by complexities and language code.
     *
     * @param pageable     {@link Pageable}.
     * @param complexities {@link List} of {@link Integer}.
     * @param languageCode language code {@link String}.
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE (( h.isCustomHabit = true AND h.userId IN (:availableUsersIds )) OR (h.isCustomHabit = false)) "
        + "AND h.complexity IN (:complexities)) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByComplexityAndLanguageCodeAndForAvailableUsersIfIsCustomHabit(Pageable pageable,
        Optional<List<Integer>> complexities,
        String languageCode, List<Long> availableUsersIds);

    /**
     * Method that find all habit's translations by tags, language code in case when
     * isCustomHabit.
     *
     * @param pageable     {@link Pageable}
     * @param tags         {@link List} of {@link String} tags
     * @param languageCode language code {@link String}
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE h.isCustomHabit = true AND h.userId IN (:availableUsersIds) AND t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByTagsAndIsCustomHabitTrueAndLanguageCode(Pageable pageable, List<String> tags,
        String languageCode, List<Long> availableUsersIds);

    /**
     * Method that find all habit's translations by tags,and language code in case
     * when isCustomHabit false.
     *
     * @param pageable     {@link Pageable}
     * @param tags         {@link List} of {@link String} tags
     * @param languageCode language code {@link String}
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE h.isCustomHabit = false AND t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByTagsAndIsCustomHabitFalseAndLanguageCode(Pageable pageable, List<String> tags,
        String languageCode);

    /**
     * Method that find all habit's translations by tags, complexities and language
     * code.
     *
     * @param pageable     {@link Pageable}.
     * @param tags         {@link List} of {@link String}.
     * @param complexities {@link List} of {@link Integer}.
     * @param languageCode language code {@link String}.
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE ((h.isCustomHabit = true AND h.userId IN (:availableUsersIds)) OR (h.isCustomHabit = false )) "
        + "AND h.complexity IN (:complexities) AND  t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByTagsAndComplexityAndLanguageCodeForAvailableUsersIfIsCustomTrue(Pageable pageable,
        List<String> tags,
        Optional<List<Integer>> complexities, String languageCode, List<Long> availableUsersIds);

    /**
     * Method that find all habit's translations in case when isCustomHabit true,
     * complexities and language code.
     *
     * @param pageable     {@link Pageable}.
     * @param complexities {@link List} of {@link Integer}.
     * @param languageCode language code {@link String}.
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT  ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE h.isCustomHabit = true AND h.userId IN (:availableUsersIds) "
        + "AND h.complexity IN (:complexities)) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByIsCustomHabitTrueAndComplexityAndLanguageCode(Pageable pageable,
        Optional<List<Integer>> complexities, String languageCode, List<Long> availableUsersIds);

    /**
     * Method that find all habit's translations by in case when isCustomHabit false
     * , complexities and language code.
     *
     * @param pageable     {@link Pageable}.
     * @param complexities {@link List} of {@link Integer}.
     * @param languageCode language code {@link String}.
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE h.isCustomHabit = false AND h.complexity IN (:complexities)) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByIsCustomHabitFalseAndComplexityAndLanguageCode(Pageable pageable,
        Optional<List<Integer>> complexities, String languageCode);

    /**
     * Method return {@link List} of {@link HabitTranslation} by habit.
     *
     * @param habit {@link Habit}.
     * @return {@link List} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     */
    List<HabitTranslation> findAllByHabit(Habit habit);
}
