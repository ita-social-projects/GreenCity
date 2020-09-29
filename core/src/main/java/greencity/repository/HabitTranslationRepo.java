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
     * Method with return {@link Optional} of {@link HabitTranslation}.
     *
     * @param name     of {@link HabitTranslation}.
     * @param language code language.
     * @return {@link Optional} of {@link HabitTranslation}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM habit_translation ht "
        + "INNER JOIN languages l ON l.id = ht.language_id "
        + "WHERE l.code = ?2 AND ht.name = ?1);")
    Optional<HabitTranslation> findByNameAndLanguageCode(String name, String language);

    /**
     * Method return {@link Optional} of {@link HabitTranslation}.
     *
     * @param habit    {@link Habit}.
     * @param language code language.
     * @return {@link HabitTranslation}.
     */
    Optional<HabitTranslation> findByHabitAndLanguageCode(Habit habit, String language);

    /**
     * Method returns available habit translations for specific user.
     *
     * @param userId   user which we use to filter.
     * @param language code language.
     * @param acquired habit acquired status
     * @return List of available {@link HabitTranslation}`s.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM habit_translation ht "
        + "INNER JOIN languages l ON l.id = ht.language_id "
        + "WHERE l.code = ?2 AND ht.habit_id IN "
        + "(SELECT ha.habit_id FROM habit_assign ha "
        + "WHERE ha.user_id = ?1 AND ha.acquired = ?3);")
    List<HabitTranslation> findHabitTranslationsByUserAndAcquiredStatus(Long userId, String language, boolean acquired);

    /**
     * Method returns available habit translations for specific user.
     *
     * @param userId   user which we use to filter.
     * @param language code language.
     * @return List of available {@link HabitTranslation}`s.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM habit_translation ht "
        + "INNER JOIN languages l ON l.id = ht.language_id "
        + "WHERE l.code = ?2 AND ht.habit_id NOT IN "
        + "(SELECT ha.habit_id FROM habit_assign ha "
        + "WHERE ha.user_id = ?1);")
    List<HabitTranslation> findAvailableHabitTranslationsByUser(Long userId, String language);

    /**
     * Method returns all habits by language.
     *
     * @param language code language.
     * @return Pageable of available {@link HabitTranslation}`s.
     * @author Dovganyuk Taras
     */
    Page<HabitTranslation> findAllByLanguageCode(Pageable pageable, String language);
}
