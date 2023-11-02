package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Provides an interface to manage {@link HabitTranslation} entity.
 *
 * @author Volodymyr Turko
 */
public interface HabitTranslationRepo extends JpaRepository<HabitTranslation, Long> {

    /**
     * Method return {@link Optional} of {@link HabitTranslation}.
     *
     * @param habit    {@link Habit}.
     * @param language code language.
     * @return {@link Optional} of {@link HabitTranslation}.
     */
    Optional<HabitTranslation> findByHabitAndLanguageCode(Habit habit, String language);

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
     * Method that finds by language code and tags all default, custom habit's
     * translations of current user or by habit assign status REQUESTED.
     *
     * @param pageable          {@link Pageable}
     * @param tags              {@link List} of {@link String} tags
     * @param languageCode      language code {@link String}
     * @param habitIdsRequested {@link List} of {@link Long} habit ids with habit
     *                          assign status REQUESTED.
     * @param userId            {@link Long} id of current user.
     *
     * @return {@link Page} of {@link HabitTranslation}.
     * @author Lilia Mokhnatska
     * @author Olena Sotnik
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE ((h.isCustomHabit = true AND (h.id IN (:habitIdsRequested) OR h.userId = :userId))  "
        + "OR (h.isCustomHabit = false)) "
        + "AND t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByTagsAndLanguageCodeAndByUserIdAndRequestedStatus(Pageable pageable,
        List<String> tags, String languageCode, List<Long> habitIdsRequested, Long userId);

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
     * Method that finds all custom habit's translations of current user or by habit
     * assign status REQUESTED by tags, complexities, language code.
     *
     * @param pageable          {@link Pageable}.
     * @param tags              {@link List} of {@link String}.
     * @param complexities      {@link List} of {@link Integer}.
     * @param languageCode      language code {@link String}.
     * @param habitIdsRequested {@link List} of {@link Long} habit ids with habit
     *                          assign status REQUESTED.
     * @return {@link Page} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     * @author Olena Sotnik
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE (h.isCustomHabit = true AND (h.id IN (:habitIdsRequested) OR h.userId = :userId)) "
        + "AND h.complexity IN (:complexities) AND t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllCustomByDifferentParametersByUserIdAndStatusRequested(Pageable pageable,
        List<String> tags, Optional<List<Integer>> complexities, String languageCode,
        List<Long> habitIdsRequested, Long userId);

    /**
     * Method that finds all custom habit's translations of current user or by habit
     * assign status REQUESTED by language code.
     *
     * @param pageable          {@link Pageable}.
     * @param languageCode      language code {@link String}.
     * @param userId            {@link Long} id of current user.
     * @param habitIdsRequested {@link List} of {@link Long} habit ids with habit
     *                          assign status REQUESTED.
     * @return {@link Page} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     * @author Olena Sotnik
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE (h.isCustomHabit = true AND (h.id IN (:habitIdsRequested) OR h.userId = :userId))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllCustomHabitsByUserIdAndStatusRequestedByLanguageCode(Pageable pageable,
        String languageCode, List<Long> habitIdsRequested, Long userId);

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
     * Method that finds by complexities and language code all default, custom
     * habit's translations of current user or by habit assign status REQUESTED.
     *
     * @param pageable          {@link Pageable}.
     * @param complexities      {@link List} of {@link Integer}.
     * @param languageCode      language code {@link String}.
     * @param userId            {@link Long} id of current user.
     * @param habitIdsRequested {@link List} of {@link Long} habit ids with habit
     *                          assign status REQUESTED.
     * @return {@link Page} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     * @author Olena Sotnik
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE ((h.isCustomHabit = true AND (h.id IN (:habitIdsRequested) OR h.userId = :userId)) "
        + "OR (h.isCustomHabit = false)) "
        + "AND h.complexity IN (:complexities)) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByComplexityAndLanguageCodeAndUserIdAndStatusRequested(Pageable pageable,
        Optional<List<Integer>> complexities, String languageCode, List<Long> habitIdsRequested, Long userId);

    /**
     * Method that finds by tags and language code all default, custom habit's
     * translations of current user or by habit assign status REQUESTED.
     *
     * @param pageable          {@link Pageable}.
     * @param tags              {@link List} of {@link String} tags.
     * @param languageCode      language code {@link String}.
     * @param userId            {@link Long} id of current user.
     * @param habitIdsRequested {@link List} of {@link Long} habit ids with habit
     *                          assign status REQUESTED.
     * @return {@link Page} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     * @author Olena Sotnik
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE (h.isCustomHabit = true AND (h.id IN (:habitIdsRequested) OR h.userId = :userId)) AND t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByTagsAndLanguageCodeAndByUserIdAndStatusRequested(Pageable pageable,
        List<String> tags, String languageCode, List<Long> habitIdsRequested, Long userId);

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
     * Method that finds by tags, complexities and language code all default, custom
     * habit's translations of current user or by habit assign status REQUESTED.
     *
     * @param pageable          {@link Pageable}.
     * @param tags              {@link List} of {@link String}.
     * @param complexities      {@link List} of {@link Integer}.
     * @param languageCode      language code {@link String}.
     * @param userId            {@link Long} id of current user.
     * @param habitIdsRequested {@link List} of {@link Long} habit ids with habit
     *                          assign status REQUESTED.
     * @return {@link Page} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     * @author Olena Sotnik
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "JOIN h.tags AS t "
        + "WHERE ((h.isCustomHabit = true AND (h.id IN (:habitIdsRequested) OR h.userId = :userId)) "
        + "OR (h.isCustomHabit = false)) "
        + "AND h.complexity IN (:complexities) AND  t.id IN "
        + "(SELECT tt.tag FROM TagTranslation AS tt "
        + "WHERE lower(tt.name) IN (:tags))) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByTagsAndComplexityAndLanguageCodeAndByUserIdAndStatusRequested(Pageable pageable,
        List<String> tags, Optional<List<Integer>> complexities, String languageCode, List<Long> habitIdsRequested,
        Long userId);

    /**
     * Method that finds by complexities and language code all custom habit's
     * translations of current user or by habit assign status REQUESTED.
     *
     * @param pageable          {@link Pageable}.
     * @param complexities      {@link List} of {@link Integer}.
     * @param languageCode      language code {@link String}.
     * @param userId            {@link Long} id of current user.
     * @param habitIdsRequested {@link List} of {@link Long} habit ids with habit
     *                          assign status REQUESTED.
     * @return {@link Page} of {@link HabitTranslation}.
     *
     * @author Lilia Mokhnatska
     * @author Olena Sotnik
     */

    @Query("SELECT DISTINCT  ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :languageCode) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE (h.isCustomHabit = true AND (h.id IN (:habitIdsRequested) OR h.userId = :userId)) "
        + "AND h.complexity IN (:complexities)) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllCustomByComplexityAndLanguageCodeAndUserIdAndStatusRequested(Pageable pageable,
        Optional<List<Integer>> complexities, String languageCode, List<Long> habitIdsRequested, Long userId);

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

    /**
     * Method that finds by language all default, custom habits of current user or
     * by habit assign status REQUESTED.
     *
     * @param pageable          {@link Pageable}.
     * @param language          code language.
     * @param habitIdsRequested {@link List} of {@link Long} habit ids with habit
     *                          assign status REQUESTED.
     * @param userId            {@link Long} id of current user.
     * @return {@link Page} of {@link HabitTranslation}`s.
     *
     * @author Olena Sotnik
     */

    @Query("SELECT DISTINCT ht FROM HabitTranslation AS ht "
        + "WHERE ht.language = "
        + "(SELECT l FROM Language AS l WHERE l.code = :language) "
        + "AND ht.habit IN "
        + "(SELECT h FROM Habit AS h "
        + "WHERE (h.isCustomHabit = true AND (h.id IN (:habitIdsRequested) OR h.userId = :userId)) "
        + "OR h.isCustomHabit = false) "
        + "ORDER BY ht.habit.id DESC")
    Page<HabitTranslation> findAllByLanguageCodeAndHabitAssignIdsRequestedAndUserId(Pageable pageable, String language,
        List<Long> habitIdsRequested, Long userId);
}
