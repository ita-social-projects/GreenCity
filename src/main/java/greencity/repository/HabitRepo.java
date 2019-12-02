package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitDictionary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Habit} entity.
 */
@Repository
public interface HabitRepo extends JpaRepository<Habit, Long> {
    /**
     * Find {@link Habit} by user.
     *
     * @param userId .
     * @return List Habit's.
     * @author Volodymyr Turko
     */
    Optional<List<Habit>> findAllByUserId(Long userId);

    /**
     * Find {@link Habit} by user and statusHabit > 0.
     * @param userId id current user.
     * @return List {@link Habit}
     */
    @Query("FROM Habit WHERE user.id = ?1 AND statusHabit > 0")
    Optional<List<Habit>> findByUserIdAndStatusHabit(Long userId);

    /**
     * Find habits by userId and HabitDictionary.id.
     *
     * @param userId id current user.
     * @param habitDictionaryId id {@link HabitDictionary}
     * @return {@link Habit}
     */
    @Query("FROM Habit WHERE user.id = ?1 AND habitDictionary.id = ?2 AND statusHabit > 0")
    Optional<Habit> findByUserIdAndHabitDictionaryId(Long userId, Long habitDictionaryId);

    /**
     * Method update habit by id and statusHabit.
     *
     * @param id {@link Habit}
     * @param status {@link Habit}
     */
    @Modifying
    @Query("UPDATE Habit SET statusHabit = ?2 WHERE id = ?1")
    void updateHabitStatusById(Long id, byte status);

    /**
     * method count user habits.
     * @param userId id current user
     * @return count habits by user
     */
    @Query("SELECT COUNT(h) FROM Habit h WHERE h.user.id = ?1 AND h.statusHabit > 0")
    int countHabitByUserId(Long userId);
}